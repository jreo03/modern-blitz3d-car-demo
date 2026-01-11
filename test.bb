Include "include/BlitzBullet.bb"

btCreateWorld(0,"license_key")
btCreateDebugDraw()
btDebugDrawSetMode(0)
btWorldSetGravity(0,-9.8,0)
btDebugDrawSetMode(0)

Include "buildcar.bb"

Graphics3D 1280, 600, 32, 2 : HWMultiTex True : SetBuffer BackBuffer()
SetFont LoadFont("Blitz")
SeedRnd MilliSecs ()

camera = CreateCamera()
CameraRange camera, 0.1, 100000
CameraClsColor camera,208,228,255
PositionEntity camera,0,5,-15
RotateEntity camera,0,0,0

;Light
BRIGHT#= 100.0
AMBIENT#= 0.7

Light = CreateLight(1)
PositionEntity Light, 5, 5, 3
PointEntity Light, CreatePivot()
LightColor Light,255*BRIGHT,225*BRIGHT,200*BRIGHT
AmbientLight 208*AMBIENT,213*AMBIENT,255*AMBIENT

testmap = LoadMesh("assets/proving_grounds/map.b3d")
btBodyCreateTrimesh(testmap,0,1)


Global car_body = LoadMesh("assets/test_car_body.b3d") : EntityFX car_body,16
;car_body = CreateCube()
Global car_body_sd = GetSurface(car_body,1)
PositionEntity car_body,0,1,0

Global car_mask = LoadTexture("assets/test_car_mask.png",4)
EntityTexture car_body,car_mask,0,0
texsizex% = TextureWidth(car_mask) : texsizey% = TextureHeight(car_mask)
fixtures_final_tex = build_texture(texsizex, texsizey, 0.7, 0.01, 0.01, "assets/test_car_fixtures.png","assets/test_car_color_mask.png")
EntityTexture car_body,fixtures_final_tex,0,1


reflectionhull = LoadMesh("assets/test_car_body.b3d",car_body)
EntityFX reflectionhull,1

Global reflection_mask = LoadTexture("assets/test_car_reflection_mask.png",4)
Global reflection = LoadTexture("assets/reflection_spec2.png",2+64)
EntityTexture reflectionhull,car_mask,0,1
EntityTexture reflectionhull,reflection_mask,0,2
EntityTexture reflectionhull,reflection,0,3
Global tex_cached = fixtures_final_tex

Global wheel_mask = LoadTexture("assets/test_car_wheel_mask.png",4)
Global wheel_tex = LoadTexture("assets/test_car_wheel.png")

Function createwheel(posx#,posy#,posz#,inverted = False)
	Local wheel = LoadMesh("assets/test_car_wheel.b3d")
	PositionEntity wheel, posx, posy, posz, False
;	
	If inverted
		ScaleEntity wheel,-1,1,1,False
		FlipMesh wheel
	EndIf	
	
	EntityTexture wheel, wheel_mask, 0, 0
	EntityTexture wheel, wheel_tex, 0, 1
	
	EntityFX wheel,2
	
	Return wheel
	
	
End Function

; assemble physics
body_reference = CreateCube()
HideEntity body_reference

car_physics = btBodyCreateTrimesh(car_body,1000)
btBodySetAngularDamping(car_physics,0.05)
btBodySetLinearDamping(car_physics,0.0031)
btBodySetRotation(car_physics,0,180,0)
btBodySetPosition(car_physics,2,10,0)

btBodyAttachEntity(car_physics,body_reference)

btBodySetAngularSleepingThreshold(car_physics,0)
btBodySetLinearSleepingThreshold(car_physics,0)

pivotCar = CreatePivot()

vehicle = btCreateVehicle(car_physics)
btVehicleSetCoordinateSystem(vehicle,0,1,2)

Local tune_rest# = 1.6
Local tune_roll# = 0.5
Local tune_frictf# = 1
Local tune_frictr# = 1

;Global wheelFLM = createwheel(0,0,0) : wb = btVehicleAddWheel(vehicle,2.15,0,-4.0) : Global wheelFL = wb
Global wheelFLM = CreatePivot() : wb = btVehicleAddWheel(vehicle,2.15,0,-4.0) : Global wheelFL = wb : HideEntity wheelFLM ; blitz3d v1.17 workaround
btWheelSetFront(wb,1)
btWheelSetRadius(wb,1)
btWheelSetFrictionSlip(wb,tune_frictf)
btWheelSetSuspensionRestLength(wb,tune_rest)
btWheelSetSuspensionStiffness(wb,13)
btWheelSetMaxSuspensionForce(wb,10000000000000000)
btWheelSetRollInFluence(wb,tune_roll)

;Global wheelFRM = createwheel(0,0,0,True) : wb = btVehicleAddWheel(vehicle,-2.15,0,-4.0) : Global wheelFR = wb
Global wheelFRM = CreatePivot() : wb = btVehicleAddWheel(vehicle,-2.15,0,-4.0) : Global wheelFR = wb : HideEntity wheelFRM
btWheelSetFront(wb,1)
btWheelSetRadius(wb,1)
btWheelSetFrictionSlip(wb,tune_frictf)
btWheelSetSuspensionRestLength(wb,tune_rest)
btWheelSetSuspensionStiffness(wb,13)
btWheelSetMaxSuspensionForce(wb,10000000000000000)
btWheelSetRollInFluence(wb,tune_roll)

;Global wheelRLM = createwheel(0,0,0) : wb = btVehicleAddWheel(vehicle,2.15,0,4.0) : Global wheelRL = wb
Global wheelRLM = CreatePivot() : wb = btVehicleAddWheel(vehicle,2.15,0,4.0) : Global wheelRL = wb : HideEntity wheelRLM
btWheelSetRadius(wb,1)
btWheelSetFrictionSlip(wb,tune_frictr)
btWheelSetSuspensionRestLength(wb,tune_rest)
btWheelSetSuspensionStiffness(wb,13)
btWheelSetMaxSuspensionForce(wb,10000000000000000)
btWheelSetRollInFluence(wb,tune_roll)

;Global wheelRRM = createwheel(0,0,0,True) : wb = btVehicleAddWheel(vehicle,-2.15,0,4.0) : Global wheelRR = wb
Global wheelRRM = CreatePivot() : wb = btVehicleAddWheel(vehicle,-2.15,0,4.0) : Global wheelRR = wb : HideEntity wheelRRM
btWheelSetRadius(wb,1)
btWheelSetFrictionSlip(wb,tune_frictr)
btWheelSetSuspensionRestLength(wb,tune_rest)
btWheelSetSuspensionStiffness(wb,13)
btWheelSetMaxSuspensionForce(wb,10000000000000000)
btWheelSetRollInFluence(wb,tune_roll)

btBodySetCenterOfMass(vehicle,0,1000,0)
btBodySetCenterOfMass(car_physics,0,1000,0)
;btBodySetCenterOfMass(car_pivot,0,1000,0)

Global VwheelFL = createwheel(0,0,0) ; blitz3d v1.17 workaround
Global VwheelFR = createwheel(0,0,0,True)
Global VwheelRL = createwheel(0,0,0)
Global VwheelRR = createwheel(0,0,0,True)

;OptimizeAlphaChannel(car_mask)
; end

SetBuffer BackBuffer()

Local inc#

generic = LoadFont("consolas",24)
header = LoadFont("consolas",32)
info = LoadFont("consolas",16,True)
SetFont generic


Local drivetrain$ = "FWD"

Local car_shadow = LoadMesh("assets/shadowbox.b3d")
Local shad_ray = btCreateRay%()
btRaySetMethodPick(shad_ray,2)

;EntityAlpha car_body,0
EntityFX car_shadow,1
EntityTexture car_shadow,LoadTexture("assets/shadow_moderate.png",1+2)
ScaleEntity car_shadow,4.3*1.2,0.05,7.8*1.2
EntityAlpha car_shadow,0.9




listener = CreateListener(camera,0.05)

enginesound = Load3DSound("assets/engine.ogg") : LoopSound enginesound
roadnoise = Load3DSound("assets/LFS_road_noise.wav") : LoopSound roadnoise
skidnoise = Load3DSound("assets/LFS_skid.wav") : LoopSound skidnoise
soundspace = EmitSound(enginesound,car_body)
roadspace = EmitSound(roadnoise,car_body)
skidspace = EmitSound(skidnoise,car_body)

Repeat	
;	time=MilliSecs ()
;	btSimulation(0.1,2)
;	New_time=MilliSecs ()-time
	PositionEntity listener,0,0,0
	
	If KeyDown(205)
		btBodyApplyTorque(car_physics,0,0,-50000)
	EndIf
	
;	btRayGetPickInfo%(ray%,
	
	If KeyHit(46)
;		Graphics3D 1200, 700,32
		FreeTexture tex_cached
		new_texture = build_texture(texsizex, texsizey, Rand(0,255)/255.0, Rand(0,255)/255.0, Rand(0,255)/255.0, "assets/test_car_fixtures.png","assets/test_car_color_mask.png")
		EntityTexture car_body,new_texture,0,1
		tex_cached = new_texture
	EndIf
	
	
	
	time=MilliSecs ()
	btSimulation(0.1,2)
	New_time=MilliSecs ()-time
	
	c_torque# = 0
	engine_braking# = 100
	
	brakef# = 0
	braker# = 0
	
	Local steering#
	Local steering_output#
	steerdesired# = 0.0
	
	
	velx# = btBodyGetLinearVelocityX#(car_physics)
	vely# = btBodyGetLinearVelocityY#(car_physics)
	velz# = btBodyGetLinearVelocityZ#(car_physics)
	
	row1# = GetMatElement#(car_body,0,0)*velx + GetMatElement#(car_body,0,1)*vely + GetMatElement#(car_body,0,2)*velz
	row2# = GetMatElement#(car_body,1,0)*velx + GetMatElement#(car_body,1,1)*vely + GetMatElement#(car_body,1,2)*velz
	row3# = GetMatElement#(car_body,2,0)*velx + GetMatElement#(car_body,2,1)*vely + GetMatElement#(car_body,2,2)*velz
	
	velocityx# = row1
	velocityy# = row2
	velocityz# = row3
	velocitylength# = Sqr(velocityx*velocityx + velocityy*velocityy + velocityz*velocityz)
	
	Local velocity_normx#
	Local velocity_normy#
	Local velocity_normz#
	
	If velocitylength>0
		velocity_normx = velocityx/velocitylength
		velocity_normy = velocityy/velocitylength
		velocity_normz = velocityz/velocitylength
	EndIf

	drifted# = ASin(velocity_normx)
;	drifted# = velocity_normx
	
	
	Local gear%
	
;	gear = 1
	
	cam_medianx# = EntityX(VwheelFL,True) + EntityX(VwheelFR,True) + EntityX(VwheelRL,True) + EntityX(VwheelRR,True)
	cam_mediany# = EntityY(VwheelFL,True) + EntityY(VwheelFR,True) + EntityY(VwheelRL,True) + EntityY(VwheelRR,True)
	cam_medianz# = EntityZ(VwheelFL,True) + EntityZ(VwheelFR,True) + EntityZ(VwheelRL,True) + EntityZ(VwheelRR,True)
	
	cam_medianx = cam_medianx/4.0
	cam_mediany = cam_mediany/4.0
	cam_medianz = cam_medianz/4.0
	
	PositionEntity pivotCar, cam_medianx, cam_mediany +1.5, cam_medianz, True
	
	
	TurnEntity camera, -10,0,0
	MoveEntity  camera, 0,-4,0
	PointEntity camera,pivotCar,0
;	PositionEntity camera, EntityX(pivotCar, True), EntityY(pivotCar, True), EntityZ(pivotCar, True), True
	PositionEntity camera, cam_medianx, cam_mediany +1.5, cam_medianz, True
	MoveEntity camera, 0,4,-16
	TurnEntity camera, 10,0,0
	maxspeed# = 20
	
	If gear = 1
		If btBodyGetLinearVelocity#(car_physics)>80
			maxspeed = 110
		ElseIf btBodyGetLinearVelocity#(car_physics)>60
			maxspeed = 80
		ElseIf btBodyGetLinearVelocity#(car_physics)>40
			maxspeed = 60
		ElseIf btBodyGetLinearVelocity#(car_physics)>20
			maxspeed = 40
		EndIf
	EndIf
	
	If KeyDown(17)
		If velocityz<2
			gear = 1
		EndIf
		
		If gear = 1
			c_torque = -min((110.0 + velocityz)*10000.0,6000.0/(maxspeed/20.0 +1))
		Else
			brakef = 50
			braker = 25
		EndIf
	EndIf
	If KeyDown(31)
		If velocityz>-2
			gear = 0
		EndIf
		If gear = 0
			c_torque = min((20.0 - velocityz)*10000.0,3000.0)
		Else
			brakef = 50
			braker = 25
		EndIf
	EndIf
	If KeyDown(57)
		braker = 100000
	EndIf
	If KeyHit(28)
		If drivetrain = "FWD"
			drivetrain = "RWD"
		ElseIf drivetrain = "RWD"
			drivetrain = "AWD"
		ElseIf drivetrain = "AWD"
			drivetrain = "FWD"
		EndIf
		btWheelSetEngineForce(wheelFL,0)
		btWheelSetEngineForce(wheelFR,0)
		btWheelSetEngineForce(wheelRL,0)
		btWheelSetEngineForce(wheelRR,0)
	EndIf
	
	steerdesired = -KeyDown(30) + KeyDown(32)
	
	turnspeed# = 0.05
	
	If (steerdesired>0 And steering<steerdesired) Or (steerdesired<0 And steering>steerdesired)
		turnspeed = 0.03
	EndIf
		
	not_standstill# = clamp(-velocityz*0.05 -1.0, 0.0, 1.0)
	us_h# = (btBodyGetLinearVelocity#(car_physics)*0.02 +1.0)
	
	If (steering<0 And drifted>0) Or (steering>0 And drifted<0)
		us_h = us_h / (Abs(drifted)/2.5 +1)
	EndIf
	
	If Abs(steerdesired - steering)>1
		turnspeed = .1*us_h
	EndIf
	steering = frict_lin(steering,steerdesired,turnspeed)
	
	steering_output = steering/(us_h*not_standstill +1)
	
	steering_output = clamp#(steering_output# -btBodyGetAngularVelocityY#(car_physics)*not_standstill,-1.0,1.0)
;	steering = clamp(steering,-1,1)
	
;	steering = 1
	
	pitch# = btBodyGetLinearVelocity#(car_physics)/maxspeed
	drifting# = min#(Abs(velocityx)/3.0,2.0)
	
	driftpitch# = 1.0 +Abs(drifted)/90.0
	
	If Abs(c_torque)>0
		ChannelVolume soundspace,0.7
	Else
		ChannelVolume soundspace,0.35
		brakef = brakef + engine_braking/maxspeed
		braker = braker + engine_braking/maxspeed
	EndIf
	ChannelVolume skidspace,drifting
	ChannelVolume roadspace,btBodyGetLinearVelocity#(car_physics)/100.0
	
	ChannelPitch soundspace, max#(pitch,0.125)*200000.0
	ChannelPitch skidspace, min#(driftpitch,1.5)*44100.0
	
	
	If drivetrain = "FWD"
		btWheelSetEngineForce(wheelFL,c_torque)
		btWheelSetEngineForce(wheelFR,c_torque)
	ElseIf drivetrain = "RWD"
		btWheelSetEngineForce(wheelRL,c_torque)
		btWheelSetEngineForce(wheelRR,c_torque)
	ElseIf drivetrain = "AWD"
		btWheelSetEngineForce(wheelFL,c_torque/2.0)
		btWheelSetEngineForce(wheelFR,c_torque/2.0)
		btWheelSetEngineForce(wheelRL,c_torque/2.0)
		btWheelSetEngineForce(wheelRR,c_torque/2.0)
	EndIf
	
	btWheelSetBreak(wheelFL,brakef)
	btWheelSetBreak(wheelFR,brakef)
	btWheelSetBreak(wheelRL,braker)
	btWheelSetBreak(wheelRR,braker)
	
	btWheelSetSteering(wheelFL,steering_output*0.6)
	btWheelSetSteering(wheelFR,steering_output*0.6)
	
	btWheelSetEntity(wheelFL,wheelFLM)
	btWheelSetEntity(wheelFR,wheelFRM)
	btWheelSetEntity(wheelRL,wheelRLM)
	btWheelSetEntity(wheelRR,wheelRRM)
	
;	btWheelSetFrictionSlip(wheelFL,tune_frict + btWheelGetContactForce#(wheelFL)*0.0001)
;	btWheelSetFrictionSlip(wheelFR,tune_frict + btWheelGetContactForce#(wheelFR)*0.0001)
;	btWheelSetFrictionSlip(wheelRL,tune_frict + btWheelGetContactForce#(wheelRL)*0.0001)
;	btWheelSetFrictionSlip(wheelRR,tune_frict + btWheelGetContactForce#(wheelRR)*0.0001)
	
	PositionEntity car_body, btBodyGetX(car_physics) + btBodyGetLinearVelocityX#(car_physics)/60, btBodyGetY(car_physics) + btBodyGetLinearVelocityY#(car_physics)/60, btBodyGetZ(car_physics)  + btBodyGetLinearVelocityZ#(car_physics)/60, True
	RotateEntity car_body, btBodyGetPitch#(car_physics) - btBodyGetAngularVelocityX#(car_physics), btBodyGetYaw#(car_physics) - btBodyGetAngularVelocityY#(car_physics), btBodyGetRoll#(car_physics) - btBodyGetAngularVelocityZ#(car_physics), True
	
	PositionEntity VwheelFL, btWheelGetX#(wheelFL), btWheelGetY#(wheelFL), btWheelGetZ#(wheelFL), True
	RotateEntity VwheelFL, btWheelGetPitch#(wheelFL), btWheelGetYaw#(wheelFL), btWheelGetRoll#(wheelFL), True
	
	PositionEntity VwheelFR, btWheelGetX#(wheelFR), btWheelGetY#(wheelFR), btWheelGetZ#(wheelFR), True
	RotateEntity VwheelFR, btWheelGetPitch#(wheelFR), btWheelGetYaw#(wheelFR), btWheelGetRoll#(wheelFR), True
	
	PositionEntity VwheelRL, btWheelGetX#(wheelRL), btWheelGetY#(wheelRL), btWheelGetZ#(wheelRL), True
	RotateEntity VwheelRL, btWheelGetPitch#(wheelRL), btWheelGetYaw#(wheelRL), btWheelGetRoll#(wheelRL), True
	
	PositionEntity VwheelRR, btWheelGetX#(wheelRR), btWheelGetY#(wheelRR), btWheelGetZ#(wheelRR), True
	RotateEntity VwheelRR, btWheelGetPitch#(wheelRR), btWheelGetYaw#(wheelRR), btWheelGetRoll#(wheelRR), True
	
	btRaySetPosition(shad_ray,btBodyGetX#(car_physics),btBodyGetY#(car_physics),btBodyGetZ#(car_physics))
	btRaySetDirection(shad_ray,0,-1,0)
	btRaySetMaxDistance(shad_ray,1000)
	btRayGetPickInfo(shad_ray,0)
	RotateEntity car_shadow, btBodyGetPitch#(car_physics) - btBodyGetAngularVelocityX#(car_physics), btBodyGetYaw#(car_physics) - btBodyGetAngularVelocityY#(car_physics), btBodyGetRoll#(car_physics) - btBodyGetAngularVelocityZ#(car_physics), True
	AlignToVector(car_shadow,btPickInfoNX(shad_ray,0),btPickInfoNY(shad_ray,0),btPickInfoNZ(shad_ray,0),2)
	PositionEntity car_shadow,btPickInfoX#(shad_ray,0),btPickInfoY#(shad_ray,0),btPickInfoZ#(shad_ray,0)
	
	
	UpdateWorld
;	btDebugDrawSetMode(DBG_RAYS+DBG_RAYCASTNORMALS)
;	btDebugDrawWorld(camera)
	RenderWorld()
	
	inc = inc + 5	
 	frames=frames+1  
   	If MilliSecs()-render_time=>1000
		fps1=frames : frames=0
		render_time=MilliSecs()  
	EndIf
	
	SetFont generic
	Color 255,255,255
	Text 10,10,"FPS: "+fps1
	Text 10,35,"steerdesired: " + steerdesired
	Text 10,60,"steering: " + steering
	Text 10,85,"steering_output: " + steering_output
	Text 10,110,"drivetrain: " + drivetrain
	Text 10,135, "understeer help amount: " + us_h
	Text 10,160, "engine pitch: " + pitch
;	Text 10,160, "wforce: " + btWheelGetContactForce#(wheelRL)
	Color 255,255,255
	Text 640,530,"speed: " + -velocityz,True,True
	Text 640,555,"gear: " + gear ,True,True
	Text 640,580,"drift angle: " + drifted ,True,True
	
	Color 255,255,255
	SetFont header
;	Text 600,300,"idk: " + (btPickInfoNY#(shad_ray))
	
	Text 10,295,"controls:"
	SetFont generic
	Text 10,330,"ESC = exit"
	Text 10,353,"Space = handbrake"
	Text 10,376,"Enter = change drivetrain"
	Text 10,399,"C = randomize color"
	Text 10,422,"WASD = you know"
	SetFont info
	Text 10,500,"project powered by Blitz3D © blitzresearch.itch.io"
	Color 255,208,20
	Text 10,520,"Bullet Physics API © Erwin Coumans,-"
	Text 10,535,"Wrapper by Mirage-lab Team"
	Flip
Until KeyHit(1)
btDestroyWorld()

End

;~IDEal Editor Parameters:
;~F#3B
;~C#Blitz3D