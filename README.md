[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Coverage Status](https://coveralls.io/repos/github/specmesh/helloworld-demo/badge.svg?branch=main)](https://coveralls.io/github/specmesh/helloworld-demo?branch=main)
[![build](https://github.com/specmesh/helloworld-demo/actions/workflows/build.yml/badge.svg)](https://github.com/specmesh/helloworld-demo/actions/workflows/build.yml)
[![CodeQL](https://github.com/specmesh/helloworld-demo/actions/workflows/codeql.yml/badge.svg)](https://github.com/specmesh/helloworld-demo/actions/workflows/codeql.yml)

# SpecMesh Hello World Demo

A Quick example of writing a spec and a test of its functionality

## Building

### Prerequisites

You'll need the Protobuf compiler `protoc` installed on your system. How to do this will vary depending on your OS

 * For **MacOS** run `brew install protobuf`. (You do have [Home brew][homeBrew] installed, right?)
 * For Linux using **Apt** package manager, run `sudo apt install protobuf-compiler`.

## Structure

### api module

Contains the [Async API spec](api/src/main/resources/specmesh-examples-schema_demo-api.yaml) that defines the
API of the domain, a.k.a. bounded context. 

### app module

This module would normally contain a microservice that implements the domain. Often there would be multiple services.
However, the actual writing to a service is outside the scope of SpecMesh.  Therefore, this module only contains
a functional test that demonstrates producing and consuming the types to the topics in the spec.

[homeBrew]: https://brew.sh/