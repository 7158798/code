/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.5.40 : Database - bill
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bill` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;

USE `bill`;

/*Table structure for table `credit_account_type` */

CREATE TABLE `credit_account_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `Identifier` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标识符',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='帐户类型';

/*Table structure for table `credit_bank` */

CREATE TABLE `credit_bank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '银行名称',
  `ext_keyword` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '扩展主题关键字',
  `online` tinyint(4) DEFAULT NULL COMMENT '是否支持网银',
  `email` tinyint(4) DEFAULT NULL COMMENT '是否支持邮件',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `code` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='银行';

/*Table structure for table `credit_bill` */

CREATE TABLE `credit_bill` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT,
  `due_date` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '到期还款日',
  `current_amount` decimal(10,2) DEFAULT NULL COMMENT '本期应还款额',
  `credits` decimal(10,2) DEFAULT NULL COMMENT '信用额度',
  `cash` decimal(10,2) DEFAULT NULL COMMENT '取现额度',
  `last_amount` decimal(10,2) DEFAULT NULL,
  `repayment` decimal(10,2) DEFAULT NULL COMMENT '还款/退货/费用返还',
  `consumption` decimal(10,2) DEFAULT NULL COMMENT '消费/取现/其他费用',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件解析模板';

/*Table structure for table `credit_bill_detail` */

CREATE TABLE `credit_bill_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bill_id` bigint(20) DEFAULT NULL COMMENT '到期还款日',
  `transaction_date` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易日期',
  `billing_date` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记账日志',
  `transaction_description` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易说明',
  `transaction_amount` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易币种/金额',
  `accountable_amount` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '入账币种/金额',
  `account_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮件解析模板';

/*Table structure for table `credit_cardtype` */

CREATE TABLE `credit_cardtype` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bank_id` bigint(20) DEFAULT NULL COMMENT '银行id',
  `name` varchar(80) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `code` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '编码',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='卡分类';

/*Table structure for table `credit_email` */

CREATE TABLE `credit_email` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_no` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商户编号',
  `email` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `password` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `download_date` datetime DEFAULT NULL COMMENT '最近下载时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮箱';

/*Table structure for table `credit_file` */

CREATE TABLE `credit_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱对应的内容文件名称',
  `subject` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '主题',
  `sent_date` datetime DEFAULT NULL COMMENT '邮件发送时间',
  `mail_type` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮件类型',
  `email` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `process_result` tinyint(4) DEFAULT NULL COMMENT '0 未处理 1 成功 2 失败',
  `status` tinyint(4) DEFAULT NULL COMMENT '有效标志1有效0无效',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_email_file_name` (`email`,`file_name`,`sent_date`)
) ENGINE=InnoDB AUTO_INCREMENT=302 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='邮箱文件';

/*Table structure for table `credit_template` */

CREATE TABLE `credit_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `due_date` varchar(200) DEFAULT NULL COMMENT '到期还款日',
  `current_amount` varchar(200) DEFAULT NULL COMMENT '本期应还款额',
  `credits` varchar(200) DEFAULT NULL COMMENT '信用额度',
  `cash` varchar(200) DEFAULT NULL COMMENT '取现额度',
  `last_amount` varchar(200) DEFAULT NULL,
  `repayment` varchar(200) DEFAULT NULL COMMENT '还款/退货/费用返还',
  `consumption` varchar(200) DEFAULT NULL COMMENT '消费/取现/其他费用',
  `transaction_date` varchar(200) DEFAULT NULL COMMENT '交易日期',
  `billing_date` varchar(200) DEFAULT NULL COMMENT '记账日志',
  `transaction description` varchar(200) DEFAULT NULL COMMENT '交易说明',
  `transaction_amount` varchar(200) DEFAULT NULL COMMENT '交易币种/金额',
  `accountable_amount` varchar(200) DEFAULT NULL COMMENT '入账币种/金额',
  `prepaid_cash_amount` varchar(200) DEFAULT NULL COMMENT '预借现金额度',
  `details` varchar(200) DEFAULT NULL COMMENT '账单明细',
  `cardtype_id` varchar(200) DEFAULT NULL COMMENT '卡类型',
  `create_date` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `transaction_description` varchar(45) DEFAULT NULL,
  `cycle` varchar(100) DEFAULT NULL COMMENT '账单周期',
  `bill_day` varchar(100) DEFAULT NULL,
  `transaction_currency` varchar(20) DEFAULT NULL COMMENT '交易币种',
  `cardholder` varchar(45) DEFAULT NULL COMMENT '持卡人',
  `credit_templatecol` varchar(45) DEFAULT NULL,
  `year_month` varchar(45) DEFAULT NULL COMMENT '年月',
  `minimum` varchar(45) DEFAULT NULL,
  `card_numbers` varchar(45) DEFAULT NULL COMMENT '解析卡号后四位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='邮件解析模板';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
