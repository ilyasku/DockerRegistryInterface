# DockerRegistryInterface

Small piece of software to read information from a Docker registry.  
So far it is really limited to read-only. The idea is to comfortably read:
* what repositories are in the registry
* what are the tags of images in a repository
* what image layers (blobs) compose an image

In addition, there is one bit more advanced feature that lets you list blobs
that you can safely delete if you would delete certain images. I.e., which 
blobs would not be part of any image anymore.

## Important note:
**I developed this application as an intern at [FLAVIA IT](flavia-it.de) 
learning some principles of software development. One of them is to write 
clean code. To test readability of pure code, there are not many comments 
(on purpose!). Feel free to ask me any questions on the code! (and give me hints
as to where I can improve readability ...)

## Install

COMING SOON

## User interfaces

COMING SOON