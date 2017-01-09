# DockerRegistryInterface

Small piece of software to read information from a Docker registry and delete images.  
The idea is to comfortably:
* read what repositories are in the registry
* read what are the tags of images in a repository
* read what image layers (blobs) compose an image
* delete images from the registry

To do so, this interface communicates with a registry via its [HTTP API v2](https://docs.docker.com/registry/spec/api/).

## Install

### Build on your machine

Gradle is used as build tool. Download/clone the content of this repository, navigate to the top-level directory, and type:
```
$ ./gradlew installDist
```
This should creates `build` folders in the top-level folder as well as in the sub-projects `docker-registry-cli` and `docker-registry-shell`. You should find executable scripts in the sub-project folders, e.g. `docker-registry-cli/build/install/docker-registry-cli/bin/docker-registry-cli`.  
Execute it to use the `docker-registry-cli` (see [below](README.md#docker-registry-cli)). There should be an executable following the same path pattern for the `docker-registry-shell`.

### Download as docker image

You do not need to build the `docker-registry-shell` locally. Instead, you can download a docker image containing it. Type:
```
$ docker pull ilyask/docker-registry-shell
```

#### Run a container

You can create a new container by typing:
```
$ docker run --name new-container-name -ti ilyask/docker-registry-shell
```
This creates a new container and the `docker-registry-shell` will not have a default URL set. It will ask you to set one now.
Next time you want to run the container, use **`start` instead of `run`**, like so:
```
docker start -i new-container-name
```

#### Build image from source

The image might be outdated, but you can build one yourself with the contents from this repository instead of downloading it from the hub. Download/clone this repository. Build the application as mentioned [above](README.md#build-on-your-machine). From within the `docker-registry-shell` sub-project directory, type:
```
$ docker build -t docker-registry-shell .
```
This should add the image named `docker-registry-shell` to your local collection of images.
Run

## User interfaces

You currently have 2 options here: a plain command line interface, `docker-registry-cli`, and an interactive one, `docker-registry-shell`. I first created the interactive one, before I knew docker implemented a working garbage collector for registries by now. **I recommend using the plain** `docker-registry-cli`, I really see no point in using the interactive shell instead.

### docker-registry-cli

You can execute this script with different arguments to read some info from your registry or delete images. Running the script with arguments should follow this syntax:
```
path/to/executable/script/docker-registry-cli <connection-options> <command> <command-arguments>
```
Connection options are optional, while a command is always required. Additionally, some commands require command arguments. This is described in the following sections in more detail.

#### Example workflow

When you use the `docker-registry-cli` for the first time, you should add the `bin` folder to your `PATH`, such that  you can run it from anywhere on your system. Next, you should set a default URL of a registry, for example to a local registry:
```
$ docker-registry-cli set-url http://localhost:5000
```
If your registry requires credentials to log in, you might also want to set default credentials:
```
$ docker-registry-cli set-credentials
```
This will prompt you to enter username and password. These defaults will be used if no connection options are provided.
Next, you might want to figure our what repositories are on your registry. List their names with:
```
$ docker-registry-cli list-repositories
```
You might want to know what images are present for a repository named "ubuntu". Images are identified by a repository and a tag name, so you would want to list the tags of the repository "ubuntu":
```
$ docker-registry-cli list-tags ubuntu
```
Let's say there are a few images with tags "1204" and "1404" that you don't want to store on your registry any longer. Type:
```
$ docker-registry-cli delete-images ubuntu:1204 ubuntu:1404
```
These images can not be pulled from your registry any longer, but this *delete* won't free up any disk space. To free up space, you would need to run the garbage collector of your registry. You can run it as in the "Run the GC" step of [this](http://stackoverflow.com/a/40293994/5829566) answer. (This requires access to the docker container!)

#### Commands

###### set-url
* **Number of expected command arguments**: 1
  1. URL of a registry.
* **Description**: Sets the default URL to the one provided as argument.
* **Example**: `$ docker-registry-cli set-url http://localhost:5000`

###### set-credentials
* **Number of expected command arguments**: 0
* **Description**: Stores your credentials locally, so you don't have to type them in over and over again. If you don't want your credentials stored locally, do not use this command. Instead, always use the `--username` or `-u` option on every call.
* **Example**: `$ docker-registry-cli set-credentials` -> this will prompt you to type in username and password.

###### list-repositories
* **Number of expected command arguments**: 0
* **Description**: Reads the repository names from the registry via HTTP and prints them.
* **Example**: `$ docker-registry-cli list-repositories`

###### list-tags
* **Number of expected command arguments**: >= 1
  * Arbitrary number of repository names, separated by spaces.
* **Description**: Reads tags of given repositories via HTTP and prints them.
* **Example**: `$ docker-registry-cli list-tags ubuntu hello-world whalesay` -> this will print all tags under repositories named "ubuntu", "hello-world" and "whalesay".

###### delete-images
* **Number of expected command arguments**: >= 1
  * Arbitrary number of image names, separated by spaces. An image name consists of a repository name and a tag name, separated by a colon, e.g. "ubuntu:1604"
* **Description**: Deletes given images via the registry's HTTP interface.
* **Example**: `$ docker-registry-cli delete-images ubuntu:1404 ubuntu:1604 whalesay:latest`

#### Connection options
If you have no default URL or credentials set via `set-url` or `set-credentials` commands, or if you want to use an URL or credentials different from the default ones, you can use connection options.

###### **--registry-url** or **--registry** or **-r**
Uses the following argument as URL for this command.
**Example**: `$ docker-registry-cli -r http://registry.my-server.com list-repositories`

###### **--user-name** or **--username** or **-u**
Uses the argument that follows as user name for this command. This will prompt for a password.
**Example**: `$ docker-registry-cli -u testuser list-repositories`

### docker-registry-shell

This is a interactive command line interface based on spring shell. It allows you to get some information on the images in your registry.

#### Example work-flow: list blobs of an image

1. `set url`: use this command to point this application to your registry's [HTTP interface](https://docs.docker.com/registry/spec/api/). You can also set the URL as default. Example: `set url http://localhost:5000 --default`.
2. `list repositories`: this command will list the names of all repositories found in your registry.
3. `list tags of`: list tags of repositories identified in step 2. Example: `list tags of ubuntu,hello-world`. (You can specify multiple repositories as shown here, comma-separated, do not use spaces!)
4. `list blobs of`: list blobs of images. An image needs to be specified using a repository name and a tag name, separated by a colon. Example: `list blobs of ubuntu:latest`.

#### Example work-flow: list blobs not used anymore if you would delete some images

**Note:** This does not delete any images. So far, no deletions are implemented in `DockerRegistryInterface`.  
Docker has finally provided a [garbage collector](https://docs.docker.com/registry/garbage-collection/), to free up disk space by deleting unused layers/blobs. This seems to work fine, but maybe you want to double check, before you delete precious images from your precious registry.

1. Follow step 1-3 from section above to set the URL and read what images are on your registry.
2. `load complete image information`: use this command to read for all images which blobs/layers are required to compose the image.
3. `load dependencies between blobs-images`: this will build a list for each blob which image depends on it.
4. `list blobs unreferenced after deletion of`: use this command to list which blobs would be unreferenced (no image would depend on it) if you would delete some images. Example: `list blobs unreferenced after deletion of ubuntu:latest,ubuntu:16.04,hello-world:latest`.
