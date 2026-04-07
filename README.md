# Java workspace (Apache OFBiz branch)

This repository’s **`main`** branch holds the **Apache HertzBeat** incident-demo fork. **This branch (`demo/ofbiz-base`) replaces the tree with [Apache OFBiz](https://ofbiz.apache.org/)** at the repository root for a Java-first demo base.

## Pinned upstream version

| Field | Value |
|--------|--------|
| Git tag | `release24.09.05` |
| Framework version | See [VERSION](VERSION) (24.09 line) |
| Upstream | [apache/ofbiz-framework](https://github.com/apache/ofbiz-framework) |

## Requirements

- **JDK 17** (full JDK, not JRE only). Set `JAVA_HOME` to a JDK 17 install. This tree uses **Gradle 7.6**; newer JDKs (e.g. 21+) can fail the build with `Unsupported class file major version` until Gradle is upgraded upstream—use JDK 17 for demos.

## Quick start (first-time)

From the repo root:

1. Initialize the Gradle wrapper if needed (see [INSTALL](INSTALL)): `./gradle/init-gradle-wrapper.sh` on Unix-like systems (`gradlew` is usually already present in this tree).
2. Load data and start (demo data; can take a while on first run):

   ```bash
   ./gradlew cleanAll loadAll
   ./gradlew ofbiz
   ```

3. Open **https://localhost:8443/webtools** (ecommerce demo: **https://localhost:8443/ecomseo**).

See [INSTALL](INSTALL) and [README.adoc](README.adoc) for seed-only installs, plugins, and full documentation: [OFBiz documentation](https://ofbiz.apache.org/documentation.html).

## Syncing with Apache

Add the ASF repo as **`upstream`** (once):

```bash
git remote add upstream https://github.com/apache/ofbiz-framework.git
```

Then:

```bash
git fetch upstream
# Merge or cherry-pick as needed, e.g. from trunk or a release tag
```

## License

Apache OFBiz is licensed under the Apache License 2.0; see [LICENSE](LICENSE) and [NOTICE](NOTICE).
