
DROP TABLE IF EXISTS `grjs_servers`;

#
# Table structure for table 'servers'
#

CREATE TABLE `grjs_servers` (
  `id` int(11) unsigned NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `project` varchar(100) NOT NULL,
  `date` int(11) unsigned,
  CONSTRAINT `uid_servers` UNIQUE (`id`),
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `grjs_server_log`;

#
# Table structure for table 'server_log'
#

CREATE TABLE `grjs_server_log` (
  `id` int(11) unsigned NOT NULL,
  `action` int(11) unsigned NOT NULL,
  `address` varchar(100) NOT NULL,
  `project`  varchar(100) NOT NULL,
  `date` int(11) unsigned,
  CONSTRAINT `fk_grjs_server_log_servers` FOREIGN KEY (`id`) REFERENCES `grjs_servers` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `grjs_client_log`;

#
# Table structure for table 'server_log'
#

CREATE TABLE `grjs_client_log` (
  `id` int(11) unsigned NOT NULL,
  `cid` int(11) unsigned NOT NULL,
  `action` int(11) unsigned NOT NULL,
  `date` int(11) unsigned,
  `duration` int(11) unsigned,
  `traficClient` int(11) unsigned,
  `traficServer` int(11) unsigned,
  CONSTRAINT `fk_grjs_client_log_servers` FOREIGN KEY (`id`) REFERENCES `grjs_servers` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

