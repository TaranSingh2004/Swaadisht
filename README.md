# Swaadisht

dependency--------------------
web, thymeleaf, data jpa, validation, security, oauth2-client, modelmapper, starter mail, lombok, devtools, mysql connector


npm init -y
npm install -D tailwindcss@3
npx tailwindcss init
add "./src/main/resources/**/*.{html,js}" on content in tailwind.config.js
see the second commit

npx tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --watch