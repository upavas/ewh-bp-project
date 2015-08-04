> DEVELOPER DOCUMENTATION

Table of Contents



# 1. Language Support #

Assuming you are a project collaborator:<br>

1. To change the language in the software, <a href='http://code.google.com/p/ewh-bp-project/source/checkout'>checkout</a> the code to your pc:<br>

2. and then go to the folder "trunk/bp-app/res/" and create a folder in the format "values-<'country_locale_code'>", for example for French it would be:<br>
<br>
trunk/bp-app/res/values-fr/<br>
<br>
3. Copy the strings.xml file in the folder  "trunk/bp-app/res/values" to the one created in 2 and translate the sentences between the tags to the<br>
appropriate language. For more information refer to the <a href='http://developer.android.com/guide/topics/resources/localization.html'>android developer page</a>.<br>
<br>
4. Commit the changes to your pc, and the push them to the google code server.<br>
<br>
Using linux terminal, it would be:<br>
$ hg status (too see that your file was modified)<br>
$ hg add (to add the new folder and file to the mercurial version control management)<br>
$ hg commit (to commit the changes locally)<br>
$ hg push (to push the changes to the remote repository)<br>

insert your username and password (from your google code profile page, not the gmail password !!!) and the code will be pushed to the remote<br>
google code repository.