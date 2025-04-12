# Swaadisht

dependency--------------------
web, thymeleaf, data jpa, validation, security, oauth2-client, modelmapper, starter mail, lombok, devtools, mysql connector


npm init -y
npm install -D tailwindcss@3
npx tailwindcss init
add "./src/main/resources/**/*.{html,js}" on content in tailwind.config.js
see the second commit

npx tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --watch

**spring security implementation**

1. Add Spring Security Dependency
2. Add role in user model
3. create custom user and implement user detials --- set role, email, password
4. create userDetialServiceImpl and implement UserDetailsService
5. create security config
6. create AuthSuccessHandler

login with google

1. OAuth2 -> add oauth2 client starter depr=endency
2. google -> a. clientid  :
   470586430884-28kobl3dij3i636g4p8r233e9t2ubh73.apps.googleusercontent.com
   b.  client secret : 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
3. add the oath login configuration:
4. login page /login and successhandler
5. in your success handler you are getting data .. save kare sakte ahin data based on provider information.