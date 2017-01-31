-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 21, 2011 at 09:42 PM
-- Server version: 5.1.41
-- PHP Version: 5.3.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `weatherproject`
--

-- --------------------------------------------------------

--
-- Table structure for table `usersTest3`
--

DROP TABLE IF EXISTS `usersTest3`;
CREATE TABLE IF NOT EXISTS `usersTest3` (
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

--
-- Dumping data for table `usersTest3`
--

INSERT INTO `usersTest3` (`userNumber`, `loginID`, `loginPassword`, `email`, `firstName`, `lastName`, `userType`, `notes`, `lastLoginTime`, `loginCount`) VALUES
(1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'cjones@bloomu.edu', 'admin', 'admin', 'Administrator', NULL, '2011-03-21 21:38:39', 2930),
(202, 'guest', '084e0343a0486ff05530df6c705c8bb4', '', 'guest', 'guest', 'Guest', NULL, '2011-03-15 18:57:43', 2),
(479, 'jjh35893', 'ab7a7855311823bb68541dc24b433494', 'jjh35893@huskies.bloomu.edu', 'JosephJ', 'Horro', 'Instructor', NULL, '2011-03-15 09:05:54', 20),
(481, 'msk51423', '72da0e40400afd22eca1d093282b229f', 'msk51423@huskies.bloomu.edu', 'MatthewS', 'Krutsick', 'Instructor', NULL, '2011-02-22 18:05:37', 7),
(489, 'jbrunski', '8f1b9beb18ad98be8cbd9d33e1bf1f84', 'jbrunski@bloomu.edu', 'Jeff', 'Brunskill', 'Administrator', NULL, '2011-03-21 14:55:47', 59),
(490, 'jbrunski_i', '8f1b9beb18ad98be8cbd9d33e1bf1f84', 'jeffbrunskill@gmail.com', 'Jeff', 'Brunskill', 'Instructor', NULL, '2011-02-17 11:22:06', 1),
(528, 'test', '912ec803b2ce49e4a541068d495ab570', 'adfasdf@asdf.com', 'testing', 'tester', 'Instructor', NULL, '2011-02-17 16:13:05', 0),
(536, 'cjones', 'af5b3d17aa1e2ff2a0f83142d692d701', 'CurtAJones@gmail.com', 'Curt', 'Jones', 'Instructor', NULL, '2011-03-15 18:59:26', 2),
(563, 'bob', 'bob', 'bob@bob.com', 'bob', 'smith', 'Student', NULL, '2011-03-02 21:32:45', 13),
(565, 'bob3', 'bob', 'bob2@bob.com', 'bob', 'smith', 'Student', NULL, '2011-02-01 21:33:19', 3),
(567, 'bob5', 'c4ca4238a0b923820dcc509a6f75849b', 'bob5@bloomu.edu', 'bob', 'bulb', 'Student', NULL, '2011-03-03 10:37:27', 0),
(568, 'bob6', 'c20ad4d76fe97759aa27a0c99bff6710', 'bob6@bu.com', 'bob', 'rob', 'Student', NULL, '2011-03-03 10:37:53', 0),
(569, 'bobbyJ', '827ccb0eea8a706c4c34a16891f84e7b', 'bobbJ@bu.com', 'bob', 'Jones', 'Instructor', NULL, '2011-03-03 10:40:55', 0),
(571, 'student', 'cd73502828457d15655bbd7a63fb0bc8', 'kvonbloh@bloomu.edu', 'student', 'student', 'Student', NULL, '2011-03-17 18:32:49', 5);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
