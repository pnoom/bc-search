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

## Rules of thumb:

1. Always try to work on the most up-to-date version of the code. (This means:
if you start a branch from unstable, do some work, but unstable is updated in
the mean time, you should pull the changes to unstable, then merge them into
your branch. This may break your branch, so fix it again, then we can merge
your branch into unstable once and for all.)

1. Always push your work to GitHub, no matter how unfinished it is. (This way,
EVERYONE can see what you have been working on, and they can adapt their work
accordingly.)

1. Don't do ANY work until you are 100% sure that you are working on roughly
the same version that everyone else is working on.

