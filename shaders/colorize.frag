varying float x;
varying float y;
uniform float width;
uniform float height;
uniform sampler2D txtr;

void main() {
	vec4 color = texture(txtr,vec2(x,y),0.0);
	gl_FragColor = vec4(0,0,0,1);

	// WHITE WALL
	// average floor
	vec4 floor = vec4(0,0,0,0);
	float N = 0.0;
	for ( float a = 0;  a < 1; a += 0.05 ) {
		floor += texture(txtr,vec2(a,0.99),0.0);
		N += 1.0;
	}
	floor /= N;
	
	if ( length(color-floor)>length(color-vec4(1,1,1,1)) ) {
		gl_FragColor = vec4(1,1,1,1);
	}

	// GREEN
	if ( color.y > color.z*1.2 && color.y > color.x*1.2 )
		gl_FragColor = vec4(0,1,0,1);
	
	// BLUE
	if ( color.x > color.y*1.2 && color.x > color.z*1.2 )
		gl_FragColor = vec4(1,0,0,1);
	
	// RED	
	if ( color.z > color.y*1.2 && color.z > color.x*1.2 )
		gl_FragColor = vec4(0,0,1,1);
}