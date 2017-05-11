# Introduction

This is a simple java, mvn, scala builder that can be used for compiling
projects.

# Build it

```
docker build --tag flinkbuilder .
```

# Pre Reqs

The flink container builder requires docker.

# Run it

First setup some aliases, and then use the build tools to generate the results:

```
alias flinkbuilder='docker run -it --rm --workdir /workspace -v $(pwd):/workspace flinkbuilder' 
flinkbuilder mvn clean package
```
