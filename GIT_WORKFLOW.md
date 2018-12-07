# Git Workflow

So far we have been working on our own branches, pushing those, then merging
into master on GitHub. The problem with this is that merging our work might
break our program, so what we have on the master branch is not a working
program. This is bad.

I think it would be best if the version that we have on master is always the
most recent, stable, working version. For this reason, we should have an
"unstable" branch on GitHub, which is where we all merge our work into. Once we
have fixed any problems that may arise from merging our separate branches of
work, we will have a working version in "unstable", which we can then merge
into master. This is the only circumstance in which changes will be made to
master.
