
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
   userNumber INT NOT NULL UNIQUE AUTO_INCREMENT,
   loginName VARCHAR(50) NOT NULL UNIQUE,
   userPassword VARCHAR(64) NOT NULL,
   firstName VARCHAR (25)DEFAULT '',
   lastName VARCHAR (35)DEFAULT '',
   emailAddress VARCHAR(50)DEFAULT '',
   userRole VARCHAR (30) NOT NULL,
   lastLoginTime VARCHAR (25),
   loginCount INT DEFAULT 0, 
   salt VARCHAR(50),
   LastAttemptedLoginTime VARCHAR(25),
   locked BOOLEAN DEFAULT FALSE,
   AttemptedLoginCount INT DEFAULT 0,
   PRIMARY KEY (userNumber)
);


CREATE INDEX user_email_index on users (emailAddress);


INSERT INTO users (loginName, userPassword,firstName,lastName,emailAddress,
userRole,lastLoginTime, loginCount, salt)
VALUES 
    # admin1234
   ('admin','1b22b95d03da1e7811dd0be2f8801e12a119a2c105f26a5bef18651f298e1121','System','Administrator','cjones@bloomu.edu','SystemAdmin','2015-02-13T20:25:25.596',0, 'me9ehohb79iobanooq0etdnc7q');


