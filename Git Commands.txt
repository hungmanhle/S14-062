##################################################
#          Super Simple Git Walkthrough          #
##################################################

#Setup Password caching
git config --global credential.helper cache
git config --global credential.helper 'cache --timeout=3600'

#Start your git repository
git clone https://github.com/DComm2013Network/BigProjectGame.git

#Set the default to no-ff merging
git config merge.ff false

#Check the status of the repository
git status

#Check to see what branch you're on and branches exist locally on your system
git branch

#Check to see what brances exist in the entire project
git branch -a

#start a new branch off master to work on your feature
git checkout -b myFeatureBranch master

#get info about current server activities
git fetch 

#OR load exisiting branch myFeature off the server
git checkout -b myFeature origin/myFeature

#Let the server know you have created a new branch so everyone else can see it
git push origin myFeatureBranch

#switch to your branch to work on your work
git checkout myFeatureBranch

#commit the changes you've made
git add -A
git commit
#this will cause a text editor to open. Type your commit message (can be multi-lined)
#then save and exit the text editor as you would normally.
#or do
git commit -m "Your commit message here"

#push your changes to the server
git checkout myFeatureBranch
git push origin myFeatureBranch

#Setup a branch you're not working on not to do a merge everytime it's pulled
git config branch.otherFeatureBranch.rebase true

#Get changes made to your branch from the server
git pull

#Get changes made to ALL branches
git pull -a

#if work has been done to branch master while you've been working on your feature
git checkout myFeatureBranch
git merge --no-ff master

#Once your done working on your feature and you're ready to merge it back to master
git checkout master
git merge --no-ff myFeatureBranch
git push

#if you want to remove the branch for the feature you were working on locally
git checkout master
git branch -d myFeatureBranch
#The branch will still exist on the server, but that's ok, good for tracking



##################################################
# EVERY TIME YOU START WORKING ON YOUR GIT REPO! #
##################################################

# Get yourself up to date with all the changes
git pull -a
git checkout myFeatureBranch


##################################################
# EVERY TIME YOU STOP WORKING ON YOUR GIT REPO!  #
##################################################

# Update everyone else
git checkout myFeatureBranch
git commit -a
git push


# Do all this and we won't have any damn collisions
# However...


##################################################
#        HOW TO HANDLE REPO COLLISIONS           #
##################################################

# When there is a collision, git will automatically add some text to your file to show where the collision has happened
"
<<<<<<< HEAD
This code is what exists on the branch getting merged into
=======
This code is what exists in what you are trying to bring in
>>>>>>> the_branch_you_are_bringing_in
"

# Change the file to whatever works the best in all spots of conflict, save and close
# Then re-add the file in colflict to the repository
git add the_file

# Your conflict should now be resolved. Try again to commit
git commit

# This should work, and your conflict is now solved