/**
 * 
 */

document.getElementById('loginButton').addEventListener('submit', function(event) {
	event.preventDefault();
	loginUser();
});

document.getElementById('signupButton').addEventListener('submit', function(event) {
	event.preventDefault(); 
	registerUser();
});


 function loginUser() {
	
	let loginUsername = document.getElementById('loginUsername').value;
	let loginPassword = document.getElementById('loginPassword').value;
	
	// Check Fiels
	if (!loginUsername) {
        alert('username is required');
        return;
    }
    if (!loginPassword) {
        alert('password is required');
        return;
    }
	
	// Login
	let url = new URL(window.location.origin + `/Assignment_4/LoginServlet`); 
  
    url.searchParams.append('loginUsername', loginUsername);
    url.searchParams.append('loginPassword', loginPassword);
    
    console.log(url); 

	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			rerouteHomeLogin(loginUsername, data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
	
 }
 
 
 function registerUser() {
	let registerEmail = document.getElementById('registerEmail').value;
	let registerUsername = document.getElementById('registerUsername').value;
	let registerPassword = document.getElementById('registerPassword').value;
	let registerConfirmPassword = document.getElementById('registerConfirmPassword').value;
    
  	// Check Fields
    if (!registerEmail) {
        alert('email is required');
        return;
    }
    if (!registerUsername) {
        alert('username is required');
        return;
    }
    if (!registerPassword) {
        alert('password is required');
        return;
    }
    if (!registerConfirmPassword) {
        alert('password confirmation is required');
        return;
    }
    if (registerPassword != registerConfirmPassword) {
       alert('passwords do not match');
        return;
    }
    
    // Check if user already exists in database with email
    let url = new URL(window.location.origin + `/Assignment_4/RegisterServlet`); 
  
    url.searchParams.append('registerEmail', registerEmail);
    url.searchParams.append('registerUsername', registerUsername);
    url.searchParams.append('registerPassword', registerPassword);
    
    console.log(url); 

	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			rerouteHome(registerUsername, data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
    
    // Else Register Them and redirect to home page
    
 }
  
 function rerouteHomeLogin(username, data) {
	console.log("reading data");
	console.log(data.result);
	// User already exists
	
	if (!data.result) {
		alert('Account does not exist in database, please register for an account');
		return;
	}
	console.log("rerouting"); 

	localStorage.setItem('user', username);
	
	window.location.href = 'index.html';
 }
 
 function rerouteHome(username, data) {
	console.log("reading data");
	console.log(data.result);
	// User already exists
	
	if (!data.result) {
		alert('email already exists in database');
		return;
	}
	console.log("rerouting"); 

	localStorage.setItem('user', username);
	
	window.location.href = 'index.html';
 }
 