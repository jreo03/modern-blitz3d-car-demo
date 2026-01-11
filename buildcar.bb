Include "include/maths.bb"
Dim layer1(0,0)
Dim layer2(0,0)
Dim color_mask#(0,0)

Function build_texture(boundsx%, boundsy%, carcolorR#, carcolorG#, carcolorB#, fixtures$, color_alpha$)
	tex_buf = CreateTexture(boundsx,boundsy)
;	Graphics texsizex,texsizey,16,3
	buffer = LoadImage(fixtures)
	
	texsizex% = ImageWidth(buffer)
	texsizey% = ImageHeight(buffer)
	
	Dim layer2(texsizex,texsizey) 
	For y = 0 To texsizey
		For x = 0 To texsizex
			LockBuffer ImageBuffer(buffer)
			layer2(x,y) = ReadPixelFast(x,y,ImageBuffer(buffer))
		Next 
	Next 
	UnlockBuffer ImageBuffer(buffer)
	
	FreeImage buffer
	
	buffer = LoadImage(color_alpha) : SetBuffer ImageBuffer(buffer)
	Dim color_mask#(texsizex,texsizey) 
	For y = 0 To texsizey
		For x = 0 To texsizex
			GetColor x,y
			color_mask(x,y) = ColorRed()
		Next 
	Next 
	
	FreeImage buffer
	
	SetBuffer TextureBuffer(tex_buf)
	For y = 0 To texsizey
		For x = 0 To texsizex
			blend# = 1 - color_mask(x,y)/255
			layer1r = carcolorR*255
			layer1g = carcolorG*255
			layer1b = carcolorB*255
			
			layer2r = Color2Red(layer2(x,y))
			layer2g = Color2Green(layer2(x,y))
			layer2b = Color2Blue(layer2(x,y))
			
			mixed = ARGB2Color(255,nlerp(layer1r,layer2r,blend,True),nlerp(layer1g,layer2g,blend,True),nlerp(layer1b,layer2b,blend,True))
			
			WritePixel x,y,mixed
		Next 
	Next 
	
	SetBuffer BackBuffer()
	
	ScaleTexture tex_buf, boundsx/Float(texsizex),boundsy/Float(texsizey)
	
	Return tex_buf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D