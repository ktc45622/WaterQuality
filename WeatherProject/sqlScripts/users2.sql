SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

DROP TABLE IF EXISTS `users2`;
CREATE TABLE IF NOT EXISTS `users2` (
  `userNumber` int(11) NOT NULL AUTO_INCREMENT,
  `loginID` varchar(100) NOT NULL,
  `loginPassword` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `firstName` varchar(30) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `userType` enum('Unregistered','Student','Instructor','Administrator','Guest') NOT NULL DEFAULT 'Student',
  `notes` text,
  `lastLoginTime` datetime NOT NULL,
  `loginCount` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userNumber`),
  UNIQUE KEY `LoginID` (`loginID`),
  UNIQUE KEY `loginID_2` (`loginID`),
  UNIQUE KEY `loginID_3` (`loginID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=573 ;

INSERT INTO `users2` (`userNumber`, `loginID`, `loginPassword`, `email`, `firstName`, `lastName`, `userType`, `notes`, `lastLoginTime`, `loginCount`) VALUES
(1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'cjones@bloomu.edu', 'admin', 'admin', 'Administrator', NULL, '2011-03-18 15:07:18', 2620),
(202, 'guest', '084e0343a0486ff05530df6c705c8bb4', '', 'guest', 'guest', 'Guest', NULL, '2011-03-15 18:57:43', 2),
(479, 'jjh35893', 'ab7a7855311823bb68541dc24b433494', 'jjh35893@huskies.bloomu.edu', 'JosephJ', 'Horro', 'Instructor', NULL, '2011-03-15 09:05:54', 20),
(481, 'msk51423', '72da0e40400afd22eca1d093282b229f', 'msk51423@huskies.bloomu.edu', 'MatthewS', 'Krutsick', 'Instructor', NULL, '2011-02-22 18:05:37', 7),
(489, 'jbrunski', '8f1b9beb18ad98be8cbd9d33e1bf1f84', 'jbrunski@bloomu.edu', 'Jeff', 'Brunskill', 'Administrator', NULL, '2011-03-17 20:43:39', 58),
(490, 'jbrunski_i', '8f1b9beb18ad98be8cbd9d33e1bf1f84', 'jeffbrunskill@gmail.com', 'Jeff', 'Brunskill', 'Instructor', NULL, '2011-02-17 11:22:06', 1),
(491, 'jbrunski_s', '8f1b9beb18ad98be8cbd9d33e1bf1f84', 'ubhockey@yahoo.com', 'Jeff', 'Brunskill', 'Student', NULL, '2011-02-16 11:35:16', 0),
(528, 'test', '912ec803b2ce49e4a541068d495ab570', 'adfasdf@asdf.com', 'testing', 'tester', 'Instructor', NULL, '2011-02-17 16:13:05', 0),
(536, 'cjones', 'af5b3d17aa1e2ff2a0f83142d692d701', 'CurtAJones@gmail.com', 'Curt', 'Jones', 'Instructor', NULL, '2011-03-15 18:59:26', 2),
(542, 'dbb76806', 'dceaaba191875fe96afd6124c04d1922', 'dbb76806@huskies.bloomu.edu', 'Daniel B', 'Balthaser', 'Student', NULL, '2011-02-25 13:46:38', 0),
(543, 'rc11392', '117047914aae73a691fc1ee8950d5ac4', 'rc11392@huskies.bloomu.edu', 'Rodrigo', 'Cano', 'Student', NULL, '2011-02-25 13:46:38', 0),
(544, 'kc70024', '492e6696b476062e6abe513affdd09f1', 'kc70024@huskies.bloomu.edu', 'Kai', 'Cui', 'Student', NULL, '2011-02-25 13:46:38', 0),
(545, 'tfe02700', '2e978d7859b81fff9f5db4d1b4f0d1a2', 'tfe02700@huskies.bloomu.edu', 'Trevor F', 'Erdley', 'Student', NULL, '2011-02-25 13:46:38', 0),
(546, 'arf88177', 'e34b180f2b762999be6822960b98a147', 'arf88177@huskies.bloomu.edu', 'Alex R', 'Funk', 'Student', NULL, '2011-02-26 10:00:41', 3),
(547, 'mog55356', '88cb0133388bc4b3adfba379d00e0647', 'mog55356@huskies.bloomu.edu', 'Matthew O', 'Ghingold', 'Student', NULL, '2011-02-25 13:46:38', 0),
(548, 'ssk61311', '0f30620318145d199607cca8a69807b9', 'ssk61311@huskies.bloomu.edu', 'Scott S', 'Kiedeisch', 'Student', NULL, '2011-02-25 13:46:39', 0),
(549, 'mpn21586', '5f3a7e64ac6279e25255a5d8643c8ebe', 'mpn21586@huskies.bloomu.edu', 'Mike P', 'Nacko', 'Student', NULL, '2011-02-25 13:46:39', 0),
(550, 'wen76675', 'f297b35d1ae463e580bbd2dde35f7969', 'wen76675@huskies.bloomu.edu', 'Wayne E', 'Nilsen', 'Student', NULL, '2011-02-25 13:46:39', 0),
(551, 'ews02808', '4a7cffca4fb9131a9743fd608e01fbdd', 'ews02808@huskies.bloomu.edu', 'Eric W', 'Subach', 'Student', NULL, '2011-02-25 13:46:39', 0),
(563, 'bob', 'bob', 'bob@bob.com', 'bob', 'smith', 'Student', NULL, '2011-03-02 21:32:45', 13),
(565, 'bob3', 'bob', 'bob2@bob.com', 'bob', 'smith', 'Student', NULL, '2011-02-01 21:33:19', 3),
(567, 'bob5', 'c4ca4238a0b923820dcc509a6f75849b', 'bob5@bloomu.edu', 'bob', 'bulb', 'Student', NULL, '2011-03-03 10:37:27', 0),
(568, 'bob6', 'c20ad4d76fe97759aa27a0c99bff6710', 'bob6@bu.com', 'bob', 'rob', 'Student', NULL, '2011-03-03 10:37:53', 0),
(569, 'bobbyJ', '827ccb0eea8a706c4c34a16891f84e7b', 'bobbJ@bu.com', 'bob', 'Jones', 'Instructor', NULL, '2011-03-03 10:40:55', 0),
(571, 'student', 'cd73502828457d15655bbd7a63fb0bc8', 'kvonbloh@bloomu.edu', 'student', 'student', 'Student', NULL, '2011-03-17 18:32:49', 5),
(572, 'bobba', '827ccb0eea8a706c4c34a16891f84e7b', 'bh@gmail.com', 'Bob', 'Fette', 'Student', NULL, '2011-03-16 15:04:31', 0);
