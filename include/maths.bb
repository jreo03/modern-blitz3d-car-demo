Function RGB2Color(Red,Green,Blue)
	Return Blue Or (Green Shl 8) Or (Red Shl 16)
End Function


Function ARGB2Color(a%, r%, g%, b%)
	Return b Or (g Shl 8) Or (r Shl 16) Or (a Shl 24)
End Function

Function Color2Alpha(dex)
	Return (dex Shr 24) And 255
End Function

Function Color2Red(dex)
	Return (dex Shr 16) And 255
End Function

Function Color2Green(dex)
	Return (dex Shr 8) And 255
End Function

Function Color2Blue(dex)
	Return dex And 255
End Function

Function clamp#(val#, min#, max#)
	result# = val
	
	If result>max
		result = max
	ElseIf result<min
		result = min
	EndIf
	
	Return result
End Function

Function nlerp#(val0#, val1#, tim#, clamped = False)
	res0# = val0
	res1# = val1
	the# = tim
	
	If clamped
		tim2# = clamp(the, 0.0, 1.0)
		Return res0*(1.0 -the) + res1*tim2
	Else
		Return res0*(1.0 -the) + res1*the
	EndIf
End Function

Function max#(val0#, val1#)
	
	If val0<val1
		Return val1
	Else
		Return val0
	EndIf
	
End Function

Function min#(val0#, val1#)
	
	If val0>val1
		Return val1
	Else
		Return val0
	EndIf
	
End Function

Function frict_lin#(val0#, val1#, friction#)
	res0# = val0
	res1# = val1
	the# = tim
	
	dist# = val0 - val1
	mdist# = Abs(dist)
	
	res0 = res0 -dist/max(mdist/friction,1)
	
	Return res0
	
	
End Function

;Print(ARGB2Color(256,256,256,256))
;
;While Not KeyHit(1) 
;Wend 
;~IDEal Editor Parameters:
;~F#19#25
;~C#Blitz3D