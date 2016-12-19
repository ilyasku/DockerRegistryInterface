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
Execute it to start the `docker-registry-shell` (see [below](README.md#docker-registry-shell)).

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

### docker-registry-shell

This is a interactive command line interface based on spring shell. It allows you to get some information on the images in your registry.

###### Example work-flow: list blobs of an image

1. `set url`: use this command to point this application to your registry's [HTTP interface](https://docs.docker.com/registry/spec/api/). You can also set the URL as default. Example: `set url http://localhost:5000 --default`.
2. `list repositories`: this command will list the names of all repositories found in your registry.
3. `list tags of`: list tags of repositories identified in step 2. Example: `list tags of ubuntu,hello-world`. (You can specify multiple repositories as shown here, comma-separated, do not use spaces!)
4. `list blobs of`: list blobs of images. An image needs to be specified using a repository name and a tag name, separated by a colon. Example: `list blobs of ubuntu:latest`.

###### Example work-flow: list blobs not used anymore if you would delete some images

**Note:** This does not delete any images. So far, no deletions are implemented in `DockerRegistryInterface`.  
Docker has finally provided a [garbage collector](https://docs.docker.com/registry/garbage-collection/), to free up disk space by deleting unused layers/blobs. This seems to work fine, but maybe you want to double check, before you delete precious images from your precious registry.

1. Follow step 1-3 from section above to set the URL and read what images are on your registry.
2. `load complete image information`: use this command to read for all images which blobs/layers are required to compose the image.
3. `load dependencies between blobs-images`: this will build a list for each blob which image depends on it.
4. `list blobs unreferenced after deletion of`: use this command to list which blobs would be unreferenced (no image would depend on it) if you would delete some images. Example: `list blobs unreferenced after deletion of ubuntu:latest,ubuntu:16.04,hello-world:latest`.
