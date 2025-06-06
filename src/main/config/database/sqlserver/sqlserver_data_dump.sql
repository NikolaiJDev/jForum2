--
-- Groups
--
SET IDENTITY_INSERT jforum_groups ON;
INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (1, 'General', 'General Users');
INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (2, 'Administration', 'Admin Users');
SET IDENTITY_INSERT jforum_groups OFF;

-- 
-- Users
--
SET IDENTITY_INSERT jforum_users ON;
INSERT INTO jforum_users ( user_id, username, user_password, user_regdate ) VALUES (1, 'Anonymous', 'nopass', GETDATE());
INSERT INTO jforum_users ( user_id, username, user_password, user_regdate, user_posts ) VALUES (2, 'Admin', '21232f297a57a5a743894a0e4a801fc3', GETDATE(), 1);
SET IDENTITY_INSERT jforum_users OFF;

--
-- User Groups
--
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (1, 1);
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 2);

--
-- Roles
--
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_vote');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_karma_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_anonymous_post');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_create_poll');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_attachments_download');

--
-- Admin
--
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_administration');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_post_remove');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_post_edit');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_topic_move');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_topic_lockUnlock');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_approve_messages');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_create_sticky_announcement_topics');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_vote');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_create_poll');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_karma_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_attachments_download');

--
-- Smilies
--
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" alt=\"smilie\" />', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" alt=\"smilie\" />', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" alt=\"smilie\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" alt=\"smilie\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" alt=\"smilie\" />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':mrgreen:', '<img src=\"#CONTEXT#/images/smilies/ed515dbff23a0ee3241dcc0a601c9ed6.gif\" alt=\"smilie\" />', 'ed515dbff23a0ee3241dcc0a601c9ed6.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-o', '<img src=\"#CONTEXT#/images/smilies/47941865eb7bbc2a777305b46cc059a2.gif\" alt=\"smilie\" />', '47941865eb7bbc2a777305b46cc059a2.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':shock:', '<img src=\"#CONTEXT#/images/smilies/385970365b8ed7503b4294502a458efa.gif\" alt=\"smilie\" />', '385970365b8ed7503b4294502a458efa.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':?:', '<img src=\"#CONTEXT#/images/smilies/0a4d7238daa496a758252d0a2b1a1384.gif\" alt=\"smilie\" />', '0a4d7238daa496a758252d0a2b1a1384.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES ('8)', '<img src=\"#CONTEXT#/images/smilies/b2eb59423fbf5fa39342041237025880.gif\" alt=\"smilie\" />', 'b2eb59423fbf5fa39342041237025880.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':lol:', '<img src=\"#CONTEXT#/images/smilies/97ada74b88049a6d50a6ed40898a03d7.gif\" alt=\"smilie\" />', '97ada74b88049a6d50a6ed40898a03d7.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':x', '<img src=\"#CONTEXT#/images/smilies/1069449046bcd664c21db15b1dfedaee.gif\" alt=\"smilie\" />', '1069449046bcd664c21db15b1dfedaee.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" alt=\"smilie\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" alt=\"smilie\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':oops:', '<img src=\"#CONTEXT#/images/smilies/499fd50bc713bfcdf2ab5a23c00c2d62.gif\" alt=\"smilie\" />', '499fd50bc713bfcdf2ab5a23c00c2d62.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':cry:', '<img src=\"#CONTEXT#/images/smilies/c30b4198e0907b23b8246bdd52aa1c3c.gif\" alt=\"smilie\" />', 'c30b4198e0907b23b8246bdd52aa1c3c.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':evil:', '<img src=\"#CONTEXT#/images/smilies/2e207fad049d4d292f60607f80f05768.gif\" alt=\"smilie\" />', '2e207fad049d4d292f60607f80f05768.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':twisted:', '<img src=\"#CONTEXT#/images/smilies/908627bbe5e9f6a080977db8c365caff.gif\" alt=\"smilie\" />', '908627bbe5e9f6a080977db8c365caff.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':roll:', '<img src=\"#CONTEXT#/images/smilies/2786c5c8e1a8be796fb2f726cca5a0fe.gif\" alt=\"smilie\" />', '2786c5c8e1a8be796fb2f726cca5a0fe.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':wink:', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (';)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (';-)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':!:', '<img src=\"#CONTEXT#/images/smilies/9293feeb0183c67ea1ea8c52f0dbaf8c.gif\" alt=\"smilie\" />', '9293feeb0183c67ea1ea8c52f0dbaf8c.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':?', '<img src=\"#CONTEXT#/images/smilies/136dd33cba83140c7ce38db096d05aed.gif\" alt=\"smilie\" />', '136dd33cba83140c7ce38db096d05aed.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':idea:', '<img src=\"#CONTEXT#/images/smilies/8f7fb9dd46fb8ef86f81154a4feaada9.gif\" alt=\"smilie\" />', '8f7fb9dd46fb8ef86f81154a4feaada9.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':arrow:', '<img src=\"#CONTEXT#/images/smilies/d6741711aa045b812616853b5507fd2a.gif\" alt=\"smilie\" />', 'd6741711aa045b812616853b5507fd2a.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':hunf:', '<img src=\"#CONTEXT#/images/smilies/0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif\" alt=\"smilie\" />', '0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" alt=\"smilie\" />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':XD:', '<img src=\"#CONTEXT#/images/smilies/49869fe8223507d7223db3451e5321aa.gif\" alt=\"smilie\" />', '49869fe8223507d7223db3451e5321aa.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':thumbup:', '<img src=\"#CONTEXT#/images/smilies/e8a506dc4ad763aca51bec4ca7dc8560.gif\" alt=\"smilie\" />', 'e8a506dc4ad763aca51bec4ca7dc8560.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':thumbdown:', '<img src=\"#CONTEXT#/images/smilies/e78feac27fa924c4d0ad6cf5819f3554.gif\" alt=\"smilie\" />', 'e78feac27fa924c4d0ad6cf5819f3554.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':|', '<img src=\"#CONTEXT#/images/smilies/1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif\" alt=\"smilie\" />', '1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':beerchug:', '<img src=\"#CONTEXT#/images/smilies/cc6690697b91b8cd32696ed6f361cbe4.gif\" alt=\"smilie\" />', 'cc6690697b91b8cd32696ed6f361cbe4.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':roflmao:', '<img src=\"#CONTEXT#/images/smilies/3c27b0c44b9f840665edd9a2d24b57f3.gif\" alt=\"smilie\" />', '3c27b0c44b9f840665edd9a2d24b57f3.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':jumping:', '<img src=\"#CONTEXT#/images/smilies/a8bb6599aceabf44663433fc99ad1db0.gif\" alt=\"smilie\" />', 'a8bb6599aceabf44663433fc99ad1db0.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':banghead:', '<img src=\"#CONTEXT#/images/smilies/973c8eb2b9dcd92cecf8187c64761ef6.gif\" alt=\"smilie\" />', '973c8eb2b9dcd92cecf8187c64761ef6.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':censored:', '<img src=\"#CONTEXT#/images/smilies/33dccaa5ed264b734370116faf09d1c8.gif\" alt=\"smilie\" />', '33dccaa5ed264b734370116faf09d1c8.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':pissed:', '<img src=\"#CONTEXT#/images/smilies/8c9fac5d3e7cba173210082669b0316e.gif\" alt=\"smilie\" />', '8c9fac5d3e7cba173210082669b0316e.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':sleep:', '<img src=\"#CONTEXT#/images/smilies/28f230537468150d34a2fb360c0d923f.gif\" alt=\"smilie\" />', '28f230537468150d34a2fb360c0d923f.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':confused:', '<img src=\"#CONTEXT#/images/smilies/4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif\" alt=\"smilie\" />', '4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif');
--
-- Demonstration Forum
--
SET IDENTITY_INSERT jforum_categories ON;
INSERT INTO jforum_categories (categories_id, title, display_order, moderated) VALUES (1,'Category Test',1,0);
SET IDENTITY_INSERT jforum_categories OFF;
SET IDENTITY_INSERT jforum_forums ON;
INSERT INTO jforum_forums (forum_id, categories_id, forum_name, forum_desc, forum_order, forum_topics, forum_last_post_id, moderated) VALUES (1,1,'Test Forum','This is a test forum',1,1,1,0);
SET IDENTITY_INSERT jforum_forums OFF;
SET IDENTITY_INSERT jforum_topics ON;
INSERT INTO jforum_topics (topic_id, forum_id, topic_title, user_id, topic_time, topic_views, topic_replies, topic_status, topic_vote_id, topic_type, topic_first_post_id, topic_last_post_id, moderated ) VALUES (1,1,'Welcome to JForum',2,GETDATE(),0,0,0,0,0,1,1,0);
SET IDENTITY_INSERT jforum_topics OFF;
SET IDENTITY_INSERT jforum_posts ON;
INSERT INTO jforum_posts (post_id, topic_id, forum_id, user_id, post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, attach, need_moderate) VALUES (1,1,1,2,GETDATE(),'127.0.0.1',1,0,1,1,null,0,1,0,0);
SET IDENTITY_INSERT jforum_posts OFF;
INSERT INTO jforum_posts_text VALUES (1,'[b][color=blue][size=18]Congratulations :!: [/size][/color][/b]\nYou have completed the installation, and JForum is up and running. \n\nTo start administering the board, login as [i]#ADMIN# / <the password you supplied in the installer>[/i] and access the [b][url=#FORUM_LINK#/admBase/login.page]Admin Control Panel[/url][/b] using the link that shows up in the bottom of the page. There you will be able to create Categories, Forums and much more  :D  \n\nFor more information and support, please refer to the following pages:\n\n:arrow: Community forum: https://community.jforum.net/\n:arrow: Documentation: https://sourceforge.net/p/jforum2/wiki2/Home\n\nThank you for choosing JForum.\n\nThe JForum Team\n\n','Welcome to JForum');

--
-- View Forum
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_forum', 1);
INSERT INTO jforum_role_values (role_id, role_value) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_forum', 2);
INSERT INTO jforum_role_values (role_id, role_value) VALUES (SCOPE_IDENTITY(), '1');

--
-- Anonymous posts
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_anonymous_post', 1);
INSERT INTO jforum_role_values (role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_anonymous_post', 2);
INSERT INTO jforum_role_values (role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

--
-- View Category
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_category', 1);
INSERT INTO jforum_role_values (role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_category', 2);
INSERT INTO jforum_role_values (role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

--
-- Create / Reply to topics
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_read_only_forums', 1);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_read_only_forums', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

-- 
-- Enable HTML
--
INSERT INTO jforum_roles (name, group_id ) VALUES ('perm_html_disabled', 1);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id ) VALUES ('perm_html_disabled', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

--
-- Attachments
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_attachments_enabled', 1);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_attachments_enabled', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

--
-- Reply only
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_only',  1);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_only', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES (SCOPE_IDENTITY(), '1');

--
-- Reply without moderation
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_without_moderation', 1); 
INSERT INTO jforum_role_values ( role_id, role_value) VALUES (SCOPE_IDENTITY(), '1'); 
    
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_without_moderation', 2); -- novo
INSERT INTO jforum_role_values ( role_id, role_value) VALUES (SCOPE_IDENTITY(), '1'); 
    
--
-- Moderation of forums
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_moderation_forums', 2); 
INSERT INTO jforum_role_values ( role_id, role_value) VALUES (SCOPE_IDENTITY(), '1'); 
