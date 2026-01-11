;*******************************************************************
;*******************************************************************
;***                                                
;***	Physics Bullet Library wrapper for Blitz3D
;***	version: 1.2.0.2
;***     
;***	(c) 2010-2011 Mirage-lab Team 
;***	www.tools.mirage-lab.com
;***	                                                                   
;*******************************************************************
;*******************************************************************

;----- Debug draw mode
Const DBG_WF = 1             ;wireframe
Const DBG_AABB = 2           ;draw aabb
Const DBG_JOINTS = 2048      ;draw joints
Const DBG_JOINTLIMITS = 4096 ;draw joint limits
Const DBG_CONTACTPOINTS = 8
Const DBG_NAMEBODY=64
Const DBG_NAMEJOINT=131072
Const DBG_RAYS=16384 
Const DBG_RAYCASTPOINTS=32768
Const DBG_RAYCASTNORMALS=65536

;----- Type Trimesh
Const TRIMESH_CONVEX = 0
Const TRIMESH_STATIC = 1
Const TRIMESH_CONCAVE = 2 

;----- Body Activation State
Const ACTIVE_TAG = 1
Const ISLAND_SLEEPING = 2
Const WANTS_DEACTIVATION = 3
Const DISABLE_DEACTIVATION = 4
Const DISABLE_SIMULATION = 5

;----- Soft Body flag collisions
Const SC_DEFAULT = 1 
Const SC_CLRS = 2     ;clusters collisions vs rigid body
Const SC_CLSELF = 64  ;clusters soft body self collisions
Const SC_CLSS = 32    ;cluster soft vs soft
Const SC_RVSMASK = 15 

;----- Solver Mode
Const SOLVER_RANDMIZE_ORDER = 1
Const SOLVER_FRICTION_SEPARATE = 2
Const SOLVER_USE_WARMSTARTING = 4
Const SOLVER_USE_FRICTION_WARMSTARTING = 8
Const SOLVER_USE_2_FRICTION_DIRECTIONS = 16
Const SOLVER_ENABLE_FRICTION_DIRECTION_CACHING = 32
Const SOLVER_DISABLE_VELOCITY_DEPENDENT_FRICTION_DIRECTION = 64
Const SOLVER_CACHE_FRIENDLY = 128
Const SOLVER_SIMD = 256
Const SOLVER_CUDA = 512

;----- Joint params
Const BT_JOINT_ERP=1
Const BT_JOINT_STOP_ERP=2
Const BT_JOINT_CFM=3
Const BT_JOINT_STOP_CFM=4

;----- Soft Body Aero Model
Const V_Point=0
Const V_TwoSided=1
Const V_OneSided=2
Const F_TwoSided=3
Const F_OneSided=4

;---- Debud Draw
Global btMeshLines=0
Dim btArrSurfaceLines(100)
For i=0 To 100 btArrSurfaceLines(i)=0 Next
Global btMeshContactLines=0
Global btSurfaceContactLines=0
Global btSurfaceCurrentId=0
Global btDebugDrawMode=0
Global btMeshLinesOrder=0

Type btDebugDrawListObjects
	Field m_Type
	Field m_Object
	Field m_r
	Field m_b
	Field m_g
End Type



Function btCreateWorld(numThreads%=0, license_key$="")
	btCreateWorldI(numThreads, license_key)
End Function

Function btCreateLine3D(x1#,y1#,z1#, x2#,y2#,z2#, cr,cg,cb, isContactLine=0)

	If btMeshLines=0 Then 
		btMeshLines=CreateMesh()
		MeshCullBox btMeshLines,-50000,-50000,-50000,100000,100000,100000
		EntityFX btMeshLines,1+2+8+16
		btArrSurfaceLines(0)=CreateSurface(btMeshLines)	
		btSurfaceCurrentId=0
		If btMeshLinesOrder<>0 Then
			EntityOrder btMeshLines,btMeshLinesOrder
		EndIf
	Else
		If (CountVertices(btArrSurfaceLines(btSurfaceCurrentId))>30000) Then
			btSurfaceCurrentId=btSurfaceCurrentId+1
			If(btArrSurfaceLines(btSurfaceCurrentId)=0)			
				btArrSurfaceLines(btSurfaceCurrentId)=CreateSurface(btMeshLines)
			EndIf	
		EndIf
	EndIf

	Local surf=0
	If isContactLine=1
		If btMeshContactLines=0
			btMeshContactLines=CreateMesh()
			MeshCullBox btMeshContactLines,-50000,-50000,-50000,100000,100000,100000
			EntityFX btMeshContactLines,1+2+8+16+32
			EntityOrder btMeshContactLines,-100
			btSurfaceContactLines=CreateSurface(btMeshContactLines)
		EndIf
		surf=btSurfaceContactLines
	Else
		surf=btArrSurfaceLines(btSurfaceCurrentId)
	EndIf	
	
	v0=AddVertex (surf,x1,y1,z1)
	v1=AddVertex (surf,x1,y1,z1)
	v2=AddVertex (surf,x2,y2,z2)
		
	VertexColor surf,v0,cr,cg,cb
	VertexColor surf,v1,cr,cg,cb
	VertexColor surf,v2,cr,cg,cb	
	
	AddTriangle surf,v0,v1,v2

End Function

Function btDebugDrawAddBody(body, red=255, green=255, blue=255)

	btListObj.btDebugDrawListObjects = New btDebugDrawListObjects
	btListObj\m_Type=0
	btListObj\m_Object=body
	btListObj\m_r=red
	btListObj\m_g=green
	btListObj\m_b=blue
	
End Function 

Function btDebugDrawBodySetColor(body, red=255, green=255, blue=255)
	For btListObj.btDebugDrawListObjects = Each btDebugDrawListObjects
		If btListObj\m_Object=body Then
			btListObj\m_r=red
			btListObj\m_g=green
			btListObj\m_b=blue
			Exit
		EndIf
	Next
End Function 

Function btDebugDrawAddJoint(joint)
	btListObj.btDebugDrawListObjects = New btDebugDrawListObjects
	btListObj\m_Type=1
	btListObj\m_Object=joint
End Function 

Function btDebugDrawRemoveObject(btObject)
	For btListObj.btDebugDrawListObjects = Each btDebugDrawListObjects
		If btListObj\m_Object=btObject Then
			Delete btListObj
			Exit
		EndIf
	Next
End Function 

Function btDebugDrawFindObject(btObject)
	For btListObj.btDebugDrawListObjects = Each btDebugDrawListObjects
		If btListObj\m_Object=btObject Then
			Return True
		EndIf
	Next
	Return False
End Function 

Function btDebugDrawAllRemoveObjects()
	For btListObj.btDebugDrawListObjects = Each btDebugDrawListObjects
		Delete btListObj
	Next
End Function 

Function btDebugDrawWorld(camera, modeDraw=0, reportWarnings=1)

	If btDebugDrawMode<>0 Then
		isDrawObject=0
		If modeDraw=0
			btDebugDrawWorldI()
			isDrawObject=1
		Else
			For btListObj.btDebugDrawListObjects = Each btDebugDrawListObjects
				Select btListObj\m_Type
				Case 0
					btDebugDrawBody(btListObj\m_Object,btListObj\m_r,btListObj\m_g,btListObj\m_b)
				Case 1
					btDebugDrawJoint(btListObj\m_Object)
				End Select
				isDrawObject=1
			Next
		EndIf

		If btDebugDrawMode<>64 Then
		If isDrawObject=1 Then
			countLines=btDebugDrawGetNumLines()
			
			bankLines=CreateBank(36)
	
			For i=0 To countLines-1
						
				btDebugDrawGetLine(i,bankLines)
						
				xfrom#=PeekFloat(bankLines,0)
				yfrom#=PeekFloat(bankLines,4)
				zfrom#=PeekFloat(bankLines,8)
						
				xto#=PeekFloat(bankLines,12)
				yto#=PeekFloat(bankLines,16)
				zto#=PeekFloat(bankLines,20)

				cvr=PeekFloat(bankLines,24)
				cvg=PeekFloat(bankLines,28)
				cvb=PeekFloat(bankLines,32)

				btCreateLine3D(xfrom,yfrom,zfrom, xto,yto,zto, cvr,cvg,cvb, False)
			Next
	
			countLineContacts=btDebugDrawGetNumContacts()
			
			For i=0 To countLineContacts-1		
				btDebugDrawGetContact(i,bankLines)
						
				xfrom#=PeekFloat(bankLines,0)
				yfrom#=PeekFloat(bankLines,4)
				zfrom#=PeekFloat(bankLines,8)
						
				xto#=PeekFloat(bankLines,12)
				yto#=PeekFloat(bankLines,16)
				zto#=PeekFloat(bankLines,20)

				cvr=PeekFloat(bankLines,24)
				cvg=PeekFloat(bankLines,28)
				cvb=PeekFloat(bankLines,32)

				btCreateLine3D(xfrom,yfrom,zfrom, xto,yto,zto, cvr,cvg,cvb, True)
			Next	

			FreeBank lineBank

			CameraClsMode(camera, 0, 0)
			WireFrame(1)
			RenderWorld()
			
			CameraClsMode(camera, 1, 1)
			WireFrame(0)

			If btMeshLines<>0 Then 
				For i=0 To btSurfaceCurrentId
					ClearSurface(btArrSurfaceLines(i),1,1)
				Next
				btSurfaceCurrentId=0
			EndIf

			If btMeshContactLines<>0 Then
				ClearSurface btSurfaceContactLines,1,1
			EndIf
		EndIf
		EndIf
		
		bankLocationText=CreateBank(24)

		height=GraphicsHeight()
		width=GraphicsWidth()

		For i=0 To btDebugDrawGetNumTexts()-1	
			DrawText$=btDebugDrawGetText(i,bankLocationText)
			CameraProject camera,PeekFloat(bankLocationText,0),PeekFloat(bankLocationText,4),PeekFloat(bankLocationText,8)
			Color PeekFloat(bankLocationText,12),PeekFloat(bankLocationText,16),PeekFloat(bankLocationText,20)
			text_posx#=ProjectedX()
			text_posy#=ProjectedY()
			If text_posx>0 And text_posx<width Then
				If text_posy>0 And text_posy<height Then
					Text text_posx,text_posy,DrawText
				EndIf
			EndIf
		Next
		Color 255,255,255
		
		FreeBank bankLocationText

		btDebugDrawClearContacts()	
		btDebugDrawClearLines()
		btDebugDrawClearTexts()
	Else
		If btMeshLines<>0 Then
			For i=0 To CountSurfaces(btMeshLines)-1 btArrSurfaceLines(i)=0 Next
			FreeEntity btMeshLines
			btMeshLines=0
		EndIf

		If btMeshContactLines<>0 Then
			FreeEntity btMeshContactLines
			btSurfaceContactLines=0
			btMeshContactLines=0
		EndIf
	EndIf

	If (reportWarnings=1)
		countWarnings=btDebugDrawGetNumReportWarnings()
		For i=0 To countWarnings-1
			btMessageBox(SystemProperty("HWND"),btDebugDrawGetReportWarning(i))
		Next
		btDebugDrawClearReportWarnings()
	EndIf
			
	

End Function

Function btBodyCreateConvexHull%(mesh%, mass#, reduction%=True)
	
	nsurf = CountSurfaces(mesh)
	nvert = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nvert = nvert + CountVertices(surf)
	Next
	vbank = CreateBank(nvert*4*3)
	nv = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nvv = CountVertices(surf)
		For nvc = 0 To nvv - 1
			PokeFloat vbank,nv*12+0,VertexX(surf,nvc)
			PokeFloat vbank,nv*12+4,VertexY(surf,nvc)
			PokeFloat vbank,nv*12+8,VertexZ(surf,nvc)
			nv = nv+1
		Next
	Next
	body=btBodyCreateConvexHullI(nvert, vbank, mass, reduction)
	FreeBank vbank
	Return body
End Function

Function btBodyCreateTrimesh%(mesh%, mass#, trimeshType%=0)
	
	nsurf = CountSurfaces(mesh)
	nvert = 0
	nface=0

	sbank=CreateBank(nsurf*4*2+4)
	PokeInt sbank,0,nsurf
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nface = nface+CountTriangles(surf)
		num=CountVertices(surf)
		nvert = nvert + num
		
		PokeInt sbank,ns*8-4,surf 
		PokeInt sbank,ns*8,num
	Next
	
	ibank = CreateBank(nface*4*3)
	nf = 0
	vbank = CreateBank(nvert*4*3)
	nv = 0
	pnvv = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nfv = CountTriangles(surf)
		For nfc = 0 To nfv -1
			PokeInt ibank,nf*12+0,TriangleVertex(surf,nfc,0)+pnvv+ns-1
			PokeInt ibank,nf*12+4,TriangleVertex(surf,nfc,1)+pnvv+ns-1
			PokeInt ibank,nf*12+8,TriangleVertex(surf,nfc,2)+pnvv+ns-1
			
			nf=nf+1
		Next
		
		nvv = CountVertices(surf)
		For nvc = 0 To nvv - 1
			PokeFloat vbank,nv*12+0,VertexX(surf,nvc)
			PokeFloat vbank,nv*12+4,VertexY(surf,nvc)
			PokeFloat vbank,nv*12+8,VertexZ(surf,nvc)
			
			nv = nv+1
		Next
		
		pnvv=pnvv+nvv-1
	Next
	body%=btBodyCreateTrimeshI(nvert, vbank, nface, ibank, mass, trimeshType)
	FreeBank vbank
	FreeBank ibank
	Return body
End Function

Function btBodyCreateMultiMaterialTrimesh%(mesh%, mass#, materials%)
	
	nsurf = CountSurfaces(mesh)
	nvert = 0
	nface=0

	sbank=CreateBank(nsurf*4*2+4)
	PokeInt sbank,0,nsurf
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nface = nface+CountTriangles(surf)
		num=CountVertices(surf)
		nvert = nvert + num
		
		PokeInt sbank,ns*8-4,surf 
		PokeInt sbank,ns*8,num
	Next
	
	ibank = CreateBank(nface*4*3)
	nf = 0
	vbank = CreateBank(nvert*4*3)
	nv = 0
	pnvv = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nfv = CountTriangles(surf)
		For nfc = 0 To nfv -1
			PokeInt ibank,nf*12+0,TriangleVertex(surf,nfc,0)+pnvv+ns-1
			PokeInt ibank,nf*12+4,TriangleVertex(surf,nfc,1)+pnvv+ns-1
			PokeInt ibank,nf*12+8,TriangleVertex(surf,nfc,2)+pnvv+ns-1
			
			nf=nf+1
		Next
		
		nvv = CountVertices(surf)
		For nvc = 0 To nvv - 1
			PokeFloat vbank,nv*12+0,VertexX(surf,nvc)
			PokeFloat vbank,nv*12+4,VertexY(surf,nvc)
			PokeFloat vbank,nv*12+8,VertexZ(surf,nvc)
			
			nv = nv+1
		Next
		
		pnvv=pnvv+nvv-1
	Next

	body%=btBodyCreateMultiMaterialTrimeshI(nvert, vbank, nface, ibank, mass, materials)
	FreeBank vbank
	FreeBank ibank
	Return body
End Function

Function btDebugDrawSetMode(mode%)

	btDebugDrawSetModeI(mode)
	btDebugDrawMode=mode
	
End Function

Function btBodyCreateTerrain%(terrain%, sx#, sy#, sz#)
    nsize% = TerrainSize (terrain)
    bank = CreateBank((nsize+1)*(nsize+1)*4)
 
    For z=0 To nsize
        For x=0 To nsize
            y# = TerrainHeight(terrain,x,z)
            PokeFloat bank,(z*(nsize+1)+x)*4,y
        Next
    Next
    body = btBodyCreateTerrainI(nsize+1,bank, sx, sy, sz)
    FreeBank(bank)
    
    Return body
End Function

Function btSoftBodyCreateFromMesh(mesh%, mass#=10, generateConstraint%=1)
	
	nsurf = CountSurfaces(mesh)
	nvert = 0
	nface=0

	sbank=CreateBank(nsurf*4*2+4)
	PokeInt sbank,0,nsurf
	
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nface = nface+CountTriangles(surf)
		num=CountVertices(surf)
		nvert = nvert + num
		
		PokeInt sbank,ns*8-4,surf 
		PokeInt sbank,ns*8,num
	Next
	
	ibank = CreateBank(nface*4*3)
	nf = 0
	vbank = CreateBank(nvert*4*3)
	nv = 0
	pnvv = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nfv = CountTriangles(surf)
		For nfc = 0 To nfv -1
			PokeInt ibank,nf*12+0,TriangleVertex(surf,nfc,0)+pnvv+ns-1
			PokeInt ibank,nf*12+4,TriangleVertex(surf,nfc,1)+pnvv+ns-1
			PokeInt ibank,nf*12+8,TriangleVertex(surf,nfc,2)+pnvv+ns-1
			nf=nf+1
		Next
		
		nvv = CountVertices(surf)
		For nvc = 0 To nvv - 1
			PokeFloat vbank,nv*12+0,VertexX(surf,nvc)
			PokeFloat vbank,nv*12+4,VertexY(surf,nvc)
			PokeFloat vbank,nv*12+8,VertexZ(surf,nvc)
			nv = nv+1
		Next
		pnvv=pnvv+nvv-1
	Next

	body%=btSoftBodyCreateFromMeshI(nvert, vbank, nface, ibank, mass, sbank, mesh, generateConstraint)
	FreeBank vbank
	FreeBank ibank
	FreeBank sbank
	Return body
End Function

Function btSoftBodyUpdateEntity(softBody%, mesh%)

	nsurf = CountSurfaces(mesh)
	nvert = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nvert = nvert+CountVertices(surf)
	Next
	
	vbank=CreateBank(nvert*4*3)
	nbank=CreateBank(nvert*4*3)
	
	btSoftBodyGetVertexPos(softBody, vbank)
	btSoftBodyGetVertexNormal(softBody, nbank)
	
	pnvert = 0
	For ns = 1 To nsurf
		surf = GetSurface(mesh,ns)
		nvert = CountVertices(surf)
		
		For i=0 To nvert - 1
			VertexCoords (surf, i, PeekFloat (vbank, (i+pnvert+ns-1)*12+0), PeekFloat(vbank, (i+pnvert+ns-1)*12+4), PeekFloat(vbank, (i+pnvert+ns-1)*12+8))
			VertexNormal (surf, i, PeekFloat (nbank, (i+pnvert+ns-1)*12+0), PeekFloat(nbank, (i+pnvert+ns-1)*12+4), PeekFloat(nbank, (i+pnvert+ns-1)*12+8))
		Next
		
		pnvert=nvert-1
	Next
	FreeBank vbank
	FreeBank nbank
End Function

Function btContactCheckBodies%(contactInfoId%, body%, mode%=0)
	Return btContactCheckBodiesI(contactInfoId, body, mode)
End Function

Function btVehicleAddWheel%(vehicle%, x#, y#, z#, global_%=0)
	Return btVehicleAddWheelI(vehicle, x, y, z, global_)
End Function 

Function btWorldSetGravity(x#, y#, z#, softWorld%=1)
	btWorldSetGravityI(x, y, z, softWorld)
End Function 

Function btSoftBodySetArrayNodes(arrNodes%=0, copyVelocityMode%=False)
	btSoftBodySetArrayNodesI(arrNodes, copyVelocityMode)
End Function 

Function btSoftBodySetTotalMass(softBody%, mass#, fromFace%=True)
	btSoftBodySetTotalMassI(softBody, mass, fromFace)
End Function 

Function btSoftBodyApplyForce(softBody%, x#, y#, z#, idNode%=-1)
	btSoftBodyApplyForceI(softBody, x, y, z, idNode)
End Function

Function btPickInfoCount%(ray%=0)
	Return btPickInfoCountI(ray)
End Function 

Function btPickInfoBody%(ray%=0, num%=0)
	Return btPickInfoBodyI(ray, num)
End Function 

Function btPickInfoDistance#(ray%=0, num%=0)
	Return btPickInfoDistanceI(ray, num)
End Function 

Function btPickInfoX#(ray%=0, num%=0)
	Return btPickInfoXI(ray, num)
End Function 

Function btPickInfoY#(ray%=0, num%=0)
	Return btPickInfoYI(ray, num)
End Function 

Function btPickInfoZ#(ray%=0, num%=0)
	Return btPickInfoZI(ray, num)
End Function 

Function btPickInfoNX#(ray%=0, num%=0)
	Return btPickInfoNXI(ray, num)
End Function 

Function btPickInfoNY#(ray%=0, num%=0)
	Return btPickInfoNYI(ray, num)
End Function 

Function btPickInfoNZ#(ray%=0, num%=0)
	Return btPickInfoNZI(ray, num)
End Function 

Function btPickInfoNode%(ray%=0, num%=0)
	Return btPickInfoNodeI(ray, num)
End Function 

Function btPickInfoTriangle%(ray%=0, num%=0)
	Return btPickInfoTriangleI(ray, num)
End Function

Function btJointSetPositionA(joint%, x#, y#, z#, global_pos%=0)
	btJointSetPositionAI(joint,x,y,z,global_pos)
End Function

Function btJointSetPositionB(joint%, x#, y#, z#, global_pos%=0)
	btJointSetPositionBI(joint,x,y,z,global_pos)
End Function

Function btJointSetRotationA(joint%, x#, y#, z#, global_rot%=0)
	btJointSetRotationAI(joint,x,y,z,global_rot)
End Function

Function btJointSetRotationB(joint%, x#, y#, z#, global_rot%=0)
	btJointSetRotationBI(joint,x,y,z,global_rot)
End Function
