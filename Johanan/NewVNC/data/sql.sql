

#
# Table structure for table 'ci_sessions'
#
DROP TABLE IF EXISTS `ci_sessions`;
CREATE TABLE IF NOT EXISTS `ci_sessions` (
        `id` varchar(128) NOT NULL,
        `ip_address` varchar(45) NOT NULL,
        `timestamp` int(10) unsigned DEFAULT 0 NOT NULL,
        `data` blob NOT NULL,
        KEY `ci_sessions_timestamp` (`timestamp`),
		PRIMARY KEY (id)
);

#
# Table structure for table 'groups'
#
DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table 'groups'
#

INSERT INTO `groups` (`id`, `name`, `description`) VALUES
     (1,'admin','Administrator'),
     (2,'user','General User');


#
# Table structure for table 'users'
#
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(45) NOT NULL,
  `username` varchar(100) NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(254) NOT NULL,
  `userid` int(11) unsigned NOT NULL,
  `activation_selector` varchar(255) DEFAULT NULL,
  `activation_code` varchar(255) DEFAULT NULL,
  `forgotten_password_selector` varchar(255) DEFAULT NULL,
  `forgotten_password_code` varchar(255) DEFAULT NULL,
  `forgotten_password_time` int(11) unsigned DEFAULT NULL,
  `remember_selector` varchar(255) DEFAULT NULL,
  `remember_code` varchar(255) DEFAULT NULL,
  `created_on` int(11) unsigned NOT NULL,
  `last_login` int(11) unsigned DEFAULT NULL,
  `active` tinyint(1) unsigned DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `uc_email` UNIQUE (`email`),
  CONSTRAINT `uc_userid` UNIQUE (`userid`),
  CONSTRAINT `uc_activation_selector` UNIQUE (`activation_selector`),
  CONSTRAINT `uc_forgotten_password_selector` UNIQUE (`forgotten_password_selector`),
  CONSTRAINT `uc_remember_selector` UNIQUE (`remember_selector`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Dumping data for table 'users'
#

INSERT INTO `users` (`id`, `ip_address`, `username`, `userid`, `password`, `email`, `activation_code`, `forgotten_password_code`, `created_on`, `last_login`, `active`, `first_name`, `last_name`, `company`, `phone`) VALUES
     ('1','127.0.0.1','administrator',0,'$2y$08$200Z6ZZbp3RAEXoaWcMA6uJOFicwNZaqk4oDhqTUiFXFe63MG.Daa','admin@admin.com','',NULL,'1268889823','1268889823','1', 'Admin','istrator','ADMIN','0');


#
# Table structure for table 'users_groups'
#
DROP TABLE IF EXISTS `users_groups`;
CREATE TABLE `users_groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `group_id` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_users_groups_users1_idx` (`user_id`),
  KEY `fk_users_groups_groups1_idx` (`group_id`),
  CONSTRAINT `uc_users_groups` UNIQUE (`user_id`, `group_id`),
  CONSTRAINT `fk_users_groups_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_groups_groups1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `users_groups` (`id`, `user_id`, `group_id`) VALUES
     (1,1,1),
     (2,1,2);


#
# Table structure for table 'login_attempts'
#
DROP TABLE IF EXISTS `login_attempts`;
CREATE TABLE `login_attempts` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(45) NOT NULL,
  `login` varchar(100) NOT NULL,
  `time` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Table structure for table 'farms'
#
DROP TABLE IF EXISTS `farms`;
CREATE TABLE `farms` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100),
  `user_id` int(11) unsigned NOT NULL,
  CONSTRAINT `fk_users_farms` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Table structure for table 'devices'
#
DROP TABLE IF EXISTS `devices`;
CREATE TABLE `devices` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `number` int(6) unsigned NOT NULL,
  `user_id` int(11) unsigned NOT NULL,
  `farm_id` int(11) unsigned,
  `name` varchar(100) NOT NULL,
  `description` varchar(100),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_users_devices` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `uc_devices_users` UNIQUE (`user_id`,`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Table structure for table 'device_connects'
#
DROP TABLE IF EXISTS `device_connects`;
CREATE TABLE `device_connects` (
  `device_id` int(11) unsigned NOT NULL,
  `time` int(11) unsigned,
  `alarm` int(1) unsigned,
  CONSTRAINT `uc_dc_device_id` UNIQUE (`device_id`),
  CONSTRAINT `fk_connects_devices` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Table structure for table 'device_connects_data'
#
DROP TABLE IF EXISTS `device_connects_data`;
CREATE TABLE `device_connects_data` (
  `device_id` int(11) unsigned NOT NULL,
  `param` int(11) unsigned,
  `value` varchar(100),
  CONSTRAINT `fk_connects_data` FOREIGN KEY (`device_id`) REFERENCES `device_connects` (`device_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `device_history_request`;
CREATE TABLE `device_history_request` (
  `device_id` int(11) unsigned NOT NULL UNIQUE,
  `hist_start` int(11) unsigned,
  `hist_end` int(11) unsigned,
  `lines` int(11) unsigned,
  `cur_line` int(11) unsigned,
  `started` int(11) unsigned,
  `finished` int(11) unsigned,
  `file_name` varchar(300),
  `remark` varchar(300),
  CONSTRAINT `fk_device_history_request` FOREIGN KEY (`device_id`) REFERENCES `devices` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
