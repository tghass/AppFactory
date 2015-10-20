CREATE DATABASE  IF NOT EXISTS `employee` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `employee`;
-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: employee
-- ------------------------------------------------------
-- Server version	5.6.26-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) DEFAULT NULL,
  `lastName` varchar(50) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `managerId` int(11) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `officePhone` varchar(50) DEFAULT NULL,
  `cellPhone` varchar(50) DEFAULT NULL,
  `pic` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'James','King','President and CEO',0,'Boston, MA','617-000-0001','781-000-0001','James_King.jpg','jking@fakemail.com'),(2,'Julie','Taylor','VP of Marketing',1,'Boston, MA','617-000-0002','781-000-0002','Julie_Taylor.jpg','jtaylor@fakemail.com'),(3,'Eugene','Lee','CFO',1,'Boston, MA','617-000-0003','781-000-0003','Eugene_Lee.jpg','elee@fakemail.com'),(4,'John','Williams','VP of Engineering',1,'Boston, MA','617-000-0004','781-000-0004','John_Williams.jpg','jwilliams@fakemail.com'),(5,'Ray','Moore','VP of Sales',1,'Boston, MA','617-000-0005','781-000-0005','Ray_Moore.jpg','rmoore@fakemail.com'),(6,'Paul','Jones','QA Manager',4,'Boston, MA','617-000-0006','781-000-0006','Paul_Jones.jpg','pjones@fakemail.com'),(7,'Paula','Gates','Software Architect',4,'Boston, MA','617-000-0007','781-000-0007','Paula_Gates.jpg','pgates@fakemail.com'),(8,'Lisa','Wong','Marketing Manager',2,'Boston, MA','617-000-0008','781-000-0008','Lisa_Wong.jpg','lwong@fakemail.com'),(9,'Gary','Donovan','Marketing Manager',2,'Boston, MA','617-000-0009','781-000-0009','Gary_Donovan.jpg','gdonovan@fakemail.com'),(10,'Kathleen','Byrne','Sales Representative',5,'Boston, MA','617-000-0010','781-000-0010','Kathleen_Byrne.jpg','kbyrne@fakemail.com'),(11,'Amy','Jones','Sales Representative',5,'Boston, MA','617-000-0011','781-000-0011','Amy_Jones.jpg','ajones@fakemail.com'),(12,'Steven','Wells','Software Architect',4,'Boston, MA','617-000-0012','781-000-0012','Steven_Wells.jpg','swells@fakemail.com');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-06 12:46:13
