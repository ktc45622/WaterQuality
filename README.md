# Water Quality Real-time monitoring station web interface

Software Engineering project repository

## Why

Potential backup repository, and hopefully where all changes are committed to first. The upsides to using BitBucket are many, from using the issue tracker to post potential bugs and updates in a centralized place (no need for emails or once-a-week meetings), detailed commit logs, a nice front-end for commit-logs, etc. As well, it allows you to comment on changes that were committed, making it possible to do peer-reviews remotely. When we need to commit work, we can handle any pull requests and merges here and push out the final update through subversion for Dr. Jones and just forward him the commit logs needed.

## How to Download

All changes should be committed through git to avoid the horrible issues of subversion.

### Clone

First you must clone the repository.

`git clone https://LouisJenkinsCS-Backup@bitbucket.org/LouisJenkinsCS-Backup/waterquality.git`

### How to add work you've done

Add all files to be committed
 
`git add --all`

Or particular files

`git add file1 file2`

### Commit changes

Create commit message (Requires an editor such as vim, emac, or nano)

`git commit`

Create commit without using text editor

`git commit -m "Message"`

Push update

`git push`

Push update to specific branch

`git push --origin branch`

### Create a branch

Branches allow for updates on a snapshot of the repository without frustrations of merge conflicts. At the end a pull request can be made to resolve this after work is done.

`git checkout -b branch`

### Pull Updates (and sync changes)

Unlike subversion, you must be up to date before pushing out an update. This prevents pushing out code that is not up to date.

`git pull`