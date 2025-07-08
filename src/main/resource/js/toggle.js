function toggle() {
	const auth = document.getElementById('auth');
	if(auth.type == "password") {
		auth.type = "text";
	} else {
		auth.type = "password";
	}
}
