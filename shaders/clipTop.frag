varying float x;
varying float y;
uniform float width;
uniform float height;
uniform sampler2D txtr;

void main() {
	vec4 color = texture2D(txtr,vec2(x,y),0.0);
	gl_FragColor = color;
	
	float dx = 1.0/width;
	float dy = 1.0/height;

	// clip top of walls
	float my = y;
	bool startedWall = false;
	while ( my < 1.0 ) {
		vec4 color = texture2D(txtr,vec2(x,my),0.0);
		
		// find bottom of wall
		if ( length(color-vec4(1,1,1,1))<0.01 && startedWall ) {
			gl_FragColor = vec4(0,0,0,0);
			return;
		}
		if ( length(color-vec4(0,0,1,1))<0.01 && startedWall ) {
			gl_FragColor = vec4(0,0,0,0);
			return;
		}
		if ( length(color-vec4(1,1,1,1))<0.01 ) {
			startedWall = true;
		} else {
			startedWall = false;
		}
		
		if ( length(color-vec4(1,0,0,1))<0.01 ) {
			gl_FragColor = vec4(0,0,0,0);
			return;
		}
		my += dy;
	}
	
	/*
	// detect ball center + radius
	int r = 6;
	int miss = 0;	
	while ( r < 170 ) {
		miss = 0;
		float start = sin(r+x*y)*3.14;
		for ( float a = start; a < start+6.28; a+=0.4 ) {
			vec3 col = texture2D(txtr,vec2(x+r*cos(a)*dx,y+r*sin(a)*dy));
			if ( !(col.x==0 && col.y==0 && col.z==1) )
				miss++;
		}
		if ( miss>=3 )
			break;
		r+=4;
	}
	if ( miss>=8 && r>=20 )
		gl_FragColor = vec4(0,0,r/100.0,1);
	else {
		if ( length(color-vec4(1,1,1,1))>0.01 )
			gl_FragColor = vec4(0,0,0,1);
	}
	*/
}