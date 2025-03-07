# Google Contacts Integration with Spring Boot & Thymeleaf

## Overview
This project is a Spring Boot web application that integrates with the Google Contacts (People) API. It enables users to authenticate using OAuth 2.0 and perform CRUD (Create, Read, Update, Delete) operations on their Google Contacts. The frontend is developed using Thymeleaf, and the backend utilizes Spring Boot and Spring Security.

## Features
- OAuth 2.0 authentication with Google
- CRUD operation on Google Contacts
- Responsive UI using Thymeleaf

## Technologies Used
- **Backend**: Spring Boot, Spring Security, Google People API
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Security & Authentication**: OAuth 2.0
- **Build Tool**: Maven

## Setup Instructions
### 1. Initialize project and gain credentials
- Obtain **Client ID** and **Client Secret**.

### 2. Clone the Repository
```sh
git clone https://github.com/quilluaz/quiloSystemIntegMidterms
```

### 3. Configure Environment Variables
Create a `.env` file in the project root and add the following:
```
GOOGLE_CLIENT_ID=(clientId)
GOOGLE_CLIENT_SECRET=(clientSecret)
```
(note: replace clientId and clientSecret with actual credentials)

## Running the Application
1. Start the application
2. Open a browser and navigate to `http://localhost:8080`.
3. Authenticate with Google and manage contacts.

## Author
Developed by Quilo, Jan Isaac S. (quilluaz)

