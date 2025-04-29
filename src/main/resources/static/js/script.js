/* Please ❤ this if you like it! */


(function($) { "use strict";

	$(function() {
		var header = $(".start-style");
		$(window).scroll(function() {
			var scroll = $(window).scrollTop();

			if (scroll >= 10) {
				header.removeClass('start-style').addClass("scroll-on");
			} else {
				header.removeClass("scroll-on").addClass('start-style');
			}
		});
	});

	//Animation

	$(document).ready(function() {
		$('body.hero-anime').removeClass('hero-anime');
	});

	//Menu On Hover

	$('body').on('mouseenter mouseleave','.nav-item',function(e){
			if ($(window).width() > 750) {
				var _d=$(e.target).closest('.nav-item');_d.addClass('show');
				setTimeout(function(){
				_d[_d.is(':hover')?'addClass':'removeClass']('show');
				},1);
			}
	});

	//Switch light/dark

	$("#switch").on('click', function () {
		if ($("body").hasClass("dark")) {
			$("body").removeClass("dark");
			$("#switch").removeClass("switched");
		}
		else {
			$("body").addClass("dark");
			$("#switch").addClass("switched");
		}
	});

  })(jQuery);


  <script th:inline="javascript">
  document.addEventListener('DOMContentLoaded', function() {
      const form = document.getElementById('userRegister');

      form.addEventListener('submit', function(e) {
          // Reset previous error states
          clearErrors();

          // Validate all fields
          const isValidName = validateName();
          const isValidEmail = validateEmail();
          const isValidPhone = validatePhone();
          const isValidPassword = validatePassword();
          const isValidConfirmPassword = validateConfirmPassword();
          const passwordsMatch = checkPasswordMatch();
          const termsChecked = validateTerms();

          // If any validation fails, prevent form submission
          if (!isValidName || !isValidEmail || !isValidPhone ||
              !isValidPassword || !isValidConfirmPassword ||
              !passwordsMatch || !termsChecked) {
              e.preventDefault();
          }
      });

      // Real-time validation for password match
      document.getElementById('pass').addEventListener('input', checkPasswordMatch);
      document.getElementById('confirmPassword').addEventListener('input', checkPasswordMatch);

      function validateName() {
          const nameInput = document.querySelector('[name="name"]');
          if (!nameInput.value.trim()) {
              showError(nameInput, 'Name is required');
              return false;
          }
          return true;
      }

      function validateEmail() {
          const emailInput = document.getElementById('email');
          const email = emailInput.value.trim();
          const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

          if (!email) {
              showError(emailInput, 'Email is required');
              return false;
          }

          if (!emailRegex.test(email)) {
              showError(emailInput, 'Please enter a valid email address');
              return false;
          }

          return true;
      }

      function validatePhone() {
          const phoneInput = document.getElementById('phone');
          const phone = phoneInput.value.trim();
          const phoneRegex = /^[0-9]{10,15}$/; // Basic phone number validation

          if (!phone) {
              showError(phoneInput, 'Phone number is required');
              return false;
          }

          if (!phoneRegex.test(phone)) {
              showError(phoneInput, 'Please enter a valid phone number');
              return false;
          }

          return true;
      }

      function validatePassword() {
          const passwordInput = document.getElementById('pass');
          const password = passwordInput.value;

          if (!password) {
              showError(passwordInput, 'Password is required');
              return false;
          }

          if (password.length < 8) {
              showError(passwordInput, 'Password must be at least 8 characters');
              return false;
          }

          return true;
      }

      function validateConfirmPassword() {
          const confirmInput = document.getElementById('confirmPassword');
          if (!confirmInput.value) {
              showError(confirmInput, 'Please confirm your password');
              return false;
          }
          return true;
      }

      function checkPasswordMatch() {
          const password = document.getElementById('pass').value;
          const confirmPassword = document.getElementById('confirmPassword').value;
          const confirmInput = document.getElementById('confirmPassword');

          if (password && confirmPassword && password !== confirmPassword) {
              showError(confirmInput, 'Passwords do not match');
              return false;
          } else {
              clearError(confirmInput);
              return true;
          }
      }

      function validateTerms() {
          const termsCheckbox = document.getElementById('terms');
          if (!termsCheckbox.checked) {
              const termsLabel = document.querySelector('label[for="terms"]');
              termsLabel.style.color = 'red';
              return false;
          }
          return true;
      }

      function showError(inputElement, message) {
          const parentDiv = inputElement.closest('div');
          let errorElement = parentDiv.querySelector('.error-message');

          if (!errorElement) {
              errorElement = document.createElement('p');
              errorElement.className = 'error-message text-red-500 text-xs mt-1';
              parentDiv.appendChild(errorElement);
          }

          errorElement.textContent = message;
          inputElement.classList.add('border-red-500');
      }

      function clearError(inputElement) {
          const parentDiv = inputElement.closest('div');
          const errorElement = parentDiv.querySelector('.error-message');

          if (errorElement) {
              errorElement.remove();
          }

          inputElement.classList.remove('border-red-500');
      }

      function clearErrors() {
          document.querySelectorAll('.error-message').forEach(el => el.remove());
          document.querySelectorAll('.border-red-500').forEach(el => el.classList.remove('border-red-500'));
          const termsLabel = document.querySelector('label[for="terms"]');
          if (termsLabel) termsLabel.style.color = '';
      }
  });
  </script>
