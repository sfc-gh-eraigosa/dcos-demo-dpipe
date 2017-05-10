# Flink setup

This folder contains a simple minimal flink setup. Documentation can
be found [here](https://ci.apache.org/projects/flink/flink-docs-release-1.2/).

# setup
Run the following script to setup:

```
./install.sh
```

# setup alias for building scala app

```
alias scalac='docker run -it --rm --workdir /workspace -v $(pwd):/workspace williamyeh/scala scalac'
alias scala='docker run -it --rm --workdir /workspace -v $(pwd):/workspace williamyeh/scala scala'
```

NOTE: If you use docker-machine in the $HOME directory, on MacOS bind mount volumes will be shared in the virtual machine.
      You should be aware of this in case you attempt to build outside of the /User directory, otherwise bind mounts won't work.

# setting up the project
