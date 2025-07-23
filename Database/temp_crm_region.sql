-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: temp_crm
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `region` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shopename` varchar(255) NOT NULL,
  `status_opening` varchar(100) DEFAULT NULL,
  `shop_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,'Grandworld','Đã đóng cửa','Đã đóng cửa'),(2,'Trần Phú Đà Nẵng','Đà Nẵng','Nhà phố'),(3,'Đảo Ngọc Ngũ Xã HN','Hà Nội','Shophouse'),(4,'Hà Nội office','Hà Nội','Khác'),(5,'Imperia Sky Garden HN','Hà Nội','Shophouse'),(6,'Vincom Bà Triệu','Hà Nội','Trong Mall'),(7,'Westpoint Phạm Hùng','Hà Nội','Shophouse'),(8,'Kosmo Tây Hồ','Hà Nội','Shophouse'),(9,'Aeon Mall Tân Phú Celadon','HCM','Trong Mall'),(10,'Crescent Mall Q7','HCM','Trong Mall'),(11,'Everrich Infinity Q.5','HCM','Shophouse'),(12,'Hoa Lan Q. PN','HCM','Nhà phố'),(13,'Millenium Apartment Q.4','Đã đóng cửa','Đã đóng cửa'),(14,'Riviera Point Q7','HCM','Shophouse'),(15,'Saigon Mia Trung Sơn','Đã đóng cửa','Đã đóng cửa'),(16,'Saigon Office','HCM','Khác'),(17,'The Botanica Q.TB','HCM','Shophouse'),(18,'The Sun Avenue','HCM','Shophouse'),(19,'Trương Định Q.3','HCM','Nhà phố'),(20,'Vincom Landmark 81','HCM','Trong Mall'),(21,'Vincom Lê Văn Việt','HCM','Trong Mall'),(22,'Vincom Phan Văn Trị','HCM','Trong Mall'),(23,'Vincom Quang Trung','HCM','Trong Mall'),(24,'Vincom Thảo Điền','HCM','Trong Mall'),(25,'Vista Verde','HCM','Shophouse'),(26,'TTTM Estella Place','HCM','Trong Mall'),(27,'SC VivoCity','HCM','Trong Mall'),(28,'Võ Thị Sáu Q.1','HCM','Nhà phố'),(29,'Midtown Q.7','HCM','Shophouse'),(30,'Aeon Mall Bình Tân','HCM','Trong Mall'),(31,'Nowzone Q.1','HCM','Trong Mall'),(32,'Parc Mall Q.8','HCM','Trong Mall'),(33,'Sương Nguyệt Ánh Q.1','HCM','Shophouse'),(34,'Saigon Centre','HCM','Trong Mall'),(35,'Xuân Thủy - Thảo Điền','HCM','Nhà phố'),(36,'THI SÁCH Q.1','HCM','Nhà phố'),(37,'FWF Vincom Plaza 3.2','HCM','Trong Mall'),(38,'Thống Nhất Vũng Tàu','Vũng Tàu','Nhà phố'),(39,'Gold Coast Nha Trang','Nha Trang','Trong Mall');
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-23 19:15:09
