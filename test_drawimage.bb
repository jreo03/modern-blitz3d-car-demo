Include "include/maths.bb"

Const texsizex = 291
Const texsizey = 337

Graphics texsizex,texsizey,16,3
carcolorR# = 0.7
carcolorG# = 0.01
carcolorB# = 0.01

buffer = LoadImage("assets/test_car_mask.png") : SetBuffer ImageBuffer(buffer)
Dim layer1(texsizex,texsizey) 
For y = 0 To texsizey
	For x = 0 To texsizex
		layer1(x,y) = ReadPixel(x,y) 
	Next 
Next 

buffer = LoadImage("assets/test_car_fixtures.png") : SetBuffer ImageBuffer(buffer)
Dim layer2(texsizex,texsizey) 
For y = 0 To texsizey
	For x = 0 To texsizex
		layer2(x,y) = ReadPixel(x,y) 
	Next 
Next 

buffer = LoadImage("assets/test_car_color_mask.png") : SetBuffer ImageBuffer(buffer)
Dim color_mask#(texsizex,texsizey) 
For y = 0 To texsizey
	For x = 0 To texsizex
		GetColor x,y
		color_mask(x,y) = ColorRed()
	Next 
Next 

SetBuffer BackBuffer() 

For y = 0 To texsizey
	For x = 0 To texsizex
		
		blend# = 1.0 -color_mask(x,y)/255
		
		
		limited1 = clamp(16777216 +layer1(x,y),0,16777215)
		limited2 = clamp(16777216 +layer2(x,y),0,16777215)
		
		layer1r = Color2Red(limited1,True)*carcolorR
		layer1g = Color2Green(limited1,True)*carcolorG
		layer1b = Color2Blue(limited1,True)*carcolorB
		
		layer2r = Color2Red(limited2,True)
		layer2g = Color2Green(limited2,True)
		layer2b = Color2Blue(limited2,True)
		
		layer1h = RGB2Color(layer1r,layer1g,layer1b); -1
		layer2h = RGB2Color(layer2r,layer2g,layer2b); -1
		
		mixed = RGB2Color(nlerp(layer1r,layer2r,blend,True),nlerp(layer1b,layer2b,blend,True),nlerp(layer1b,layer2b,blend,True))
		
		col = layer1(x,y)
		
		WritePixel x,y,clamp(mixed,0,16777215)
	Next 
Next 


While Not KeyHit(1) 
Wend 
;~IDEal Editor Parameters:
;~C#Blitz3D