[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Coverage Status](https://coveralls.io/repos/github/specmesh/specmesh-template/badge.svg?branch=main)](https://coveralls.io/github/specmesh/specmesh-template?branch=main)
[![build](https://github.com/specmesh/specmesh-template/actions/workflows/build.yml/badge.svg)](https://github.com/specmesh/specmesh-template/actions/workflows/build.yml)
[![CodeQL](https://github.com/specmesh/specmesh-template/actions/workflows/codeql.yml/badge.svg)](https://github.com/specmesh/specmesh-template/actions/workflows/codeql.yml)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/specmesh/specmesh-template/badge)](https://api.securityscorecards.dev/projects/github.com/specmesh/specmesh-template) <!--- init:remove --->

# SpecMesh Template Repository

A template repositories for users to quickly bootstrap new repositories.

Click the [Use this template][useThisTemplate] to create a new repository from this template.

Once the `bootstrap` workflow has completed, pull down locally and run the `.specmesh/clean_up.sh` script, committing the changes.

## Using this template to create a SpecMesh tutorial or demo

If using this template to create a new Creek Tutorial, then there are some additional steps required:

1. Customise the repositories settings in GitHub:
    1. General
        1. disable wiki
        2. enable discussions
        3. only allow squash merging
        4. allow auto-merge
        5. auto delete branches
    2. Branches
        1. Protect main branch
            1. Require PR
                1. Require approval
                2. Dismiss stale
            2. Require status checks
                1. CodeQL
                2. build
                3. build_pages
                4. coveralls
    3. Pages
        1. Build from actions
        2. enforce https
    4. Coverage
       1. Add the repo to coveralls.io
       2. Set the `COVERALLS_REPO_TOKEN` secret in GitHub for the repository
2. Add the new tutorial to the SpecMesh website

[useThisTemplate]: https://github.com/specmesh/specmesh-template/generate

