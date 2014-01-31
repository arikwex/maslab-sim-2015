varying float x;
varying float y;
uniform float width;
uniform float height;
uniform sampler2D txtr;

void main() {
	vec4 original = texture2D(txtr,vec2(x,y),0.0);
	gl_FragColor = original;
	
	float dx = 1.0/width;
	float dy = 1.0/height;

	// clip top of walls
	float my = y+dy;
	bool startedWall = false;
	while ( my < 1.0 ) {
		vec4 color = texture2D(txtr,vec2(x,my),0.0);
		
		// scroll down until meeting a blue or teal color, then sepukku
		if ( length(color-vec4(1,0,0,1))<0.01 || length(color-vec4(1,1,0,1))<0.01 ) {
			color = texture2D(txtr,vec2(x,my+2.0*dy),0.0);
			//double check
			if ( length(color-vec4(1,0,0,1))<0.01 || length(color-vec4(1,1,0,1))<0.01 ) {
				gl_FragColor = vec4(0,0,0,1);
				return;
			}
		}
		
		my += dy;
	}
	
	if ( length(original-vec4(0,1,0,1))<0.01 || length(original-vec4(1,0,0,1))<0.01 ) {
		return;
	}
	
	vec4 beneath = texture2D(txtr,vec2(x,y+dy),0.0);
	if ( length(original-vec4(1,1,1,1))<0.01 && length(beneath-vec4(0,0,0,1))<0.01 ) {
		gl_FragColor = vec4(1,1,1,1);
		
		// leave lowest valid white trail only
		my = y-dy;
		int consecutive = 0;
		
		while ( my > 0.0 ) {
			vec4 color = texture2D(txtr,vec2(x,my),0.0);
			
			// Early cutoff for finding blue or teal
			if ( length(color-vec4(1,0,0,1))<0.01 || length(color-vec4(1,1,0,1))<0.01 ) {
				return;
			}
			
			// Punishment for being a false white
			if ( consecutive>8  ) {
				gl_FragColor = vec4(0,0,0,1);
				return;
			}
			
			
			if ( length(color-vec4(0,0,0,1)) < 0.01 ) {
				consecutive++;
			} else {
				consecutive=0;
			}
			
			my -= dy;
		}
	} else {
		if ( length(original-vec4(1,1,1,1))<0.01 )
			gl_FragColor = vec4(0,0,0,1);
	}
}