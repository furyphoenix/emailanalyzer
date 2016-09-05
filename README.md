# emailanalyzer
邮件分析系统。包括邮件导入导出、正文与附件内容检索、来往分析、邮件溯源、要素（命名实体）自动提取检索分析、邮件网络分析、发件规律分析、社团分析等系列功能。
源码为Eclipse工程。因github文件限制，项目中所需的数据文件（如语言模型、结构化数据表等），存放在百度盘中，链接: https://pan.baidu.com/s/1boQ4mjP 密码: c3dx。请下载解压到工程文件根目录下（文件夹为data）。
系统需MySQL 5.5以上版本数据库支持，在运行前，请将data目录下knowledgedb.sql导入至MySQL数据库中。脚本包含建表建库指令。
邮件分析系统的启动类为neos.app.email.gui.EmailMainWin。
