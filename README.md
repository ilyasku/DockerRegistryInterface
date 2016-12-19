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
(on purpose!).** Feel free to ask me any questions on the code. (and give me hints
where I can improve readability ...)

## Install

### Build on your machine

Gradle is used as build tool. Download/clone the content of this repository, navigate to the top-level directory, and type:
```
$ ./gradlew installDist
```
This should create a `build` folder. You should find an executable script at `build/install/DockerRegistryInterface/bin/DockerRegistryInterface`.
Execute it to start the `docker-registry-shell` (see section User interfaces below).

### Download as docker image

You do not need to build the docker-registry-shell locally. Instead, you can download a docker image containing it. Type:
```
$ docker pull ilyask/docker-registry-shell
```

###### Run a container

You can create a new container by typing:
```
$ docker run --name new-container-name -ti ilyask/docker-registry-interface
```
This creates a new container and the `docker-registry-shell` will not have a default URL set. It will ask you to set one now.
Next time you want to run the container, use **`start` instead of `run`**, like so:
```
docker start -i new-container-name
```
###### Build image from source

The image might be outdated, but you can build one yourself with the contents from this repository instead of downloading it from the hub. Download/clone this repository. Build the application as mentioned [above](README.md#build-on-your-machine). From within the projects top-level directory, type:
```
$ docker build -t docker-registry-shell .
```
This should add the image named `docker-registry-shell` to your local collection of images.
Run

## User interfaces

COMING SOON
