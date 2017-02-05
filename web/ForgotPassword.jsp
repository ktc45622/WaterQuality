<!DOCTYPE html>
<%--ForgotPassword.jsp--%>
<html>
    <head>
        <title>Forgot Password</title> <%--Title Bar--%>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<link rel="stylesheet" href="./styles/generalStyles.css" type="text/css">
    </head>
    <body>
	    
        <header class="title_bar_container">
            <div id="HeaderText">Reset Password Page</div>
        </header>
        <img id="backPhoto" src="./images/backgroundImage.JPG"> <%--Sets the background--%>
           
        <section class = "content_container" id = "small_container">

            <header class = "content_title_bar" id="login_header"> 
                <div class = "title" >
                    Request a new password for your account
                </div> 
            </header>
                
            <form id="login_form" method="POST" action="ForgotPasswordServlet">  
                <div>
                    <img id="huskyIcon" src="./images/husky.png">
                    <div id="forgetMessage">${message}</div>
                </div>
                <div id = "login_text_container">
                
                Email Address: <input type="email" name="emailAddress"> 
                <br>
                <button type="submit" id="resetSubmit" value="Submit" class ="btn btn-4 btn-4d icon-submit" 
                        name="submit">Submit</button>
                <button type="button" id="resetGoBack" class ="btn btn-4 btn-4d icon-arrow-left" name="goback" 
                        onclick="location.href='/WaterQuality/';">Cancel</button>
                </div>
            </form>
        </section>
    </body>
</html>
