const container = document.querySelector('.container');
const registerBtn = document.querySelector('.register-btn');
const loginBtn = document.querySelector('.login-btn');

registerBtn.addEventListener('click', () => {
    container.classList.add('active');
})

loginBtn.addEventListener('click', () => {
    container.classList.remove('active');
})

    document.addEventListener('DOMContentLoaded', function() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');
        const registerBtn = document.querySelector('.register-btn');
        const loginBtn = document.querySelector('.login-btn');
        const container = document.getElementById('authContainer');

        // Show container after initialization
        container.classList.add('active');

        // Check URL to determine which form to show
        if (window.location.pathname === '/register') {
            showRegisterForm();
        }

        // Button event listeners
        registerBtn.addEventListener('click', showRegisterForm);
        loginBtn.addEventListener('click', showLoginForm);

        function showRegisterForm() {
            loginForm.style.display = 'none';
            registerForm.style.display = 'block';
            container.classList.add('active');
            // Update URL without reload
            history.pushState(null, '', '/register');
        }

        function showLoginForm() {
            registerForm.style.display = 'none';
            loginForm.style.display = 'block';
            container.classList.add('active');
            // Update URL without reload
            history.pushState(null, '', '/login');
        }
    });
