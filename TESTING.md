# Testing Documentation

## Overview

This project includes a comprehensive testing suite that covers the core functionality of the Combat Tag mod. The tests are automatically run by GitHub Actions on every push and pull request.

## Running Tests Locally

### Run all tests
```bash
./gradlew test
```

### Run tests with coverage report
```bash
./gradlew test jacocoTestReport
```

The coverage report will be generated at `build/reports/jacoco/html/index.html`

### Run specific test class
```bash
./gradlew test --tests "name.modid.ConfigTest"
```

### Run tests with verbose output
```bash
./gradlew test --info
```

## Test Structure

The test suite is organized as follows:

```
src/test/java/name/modid/
├── ConfigTest.java                    # Tests for configuration management
├── CombatBarTest.java                 # Tests for boss bar display
├── CombatBarManagerTest.java          # Tests for boss bar management
├── CombatCooldownManagerTest.java     # Tests for item cooldown management
├── CombatTagTest.java                 # Tests for main mod class and weapon sets
└── events/
    ├── PlayerAttackCallbackTest.java  # Tests for player attack events
    ├── PlayerDamageCallbackTest.java  # Tests for player damage events
    └── PlayerDeathCallbackTest.java   # Tests for player death events
```

## Test Coverage

The project uses JaCoCo for code coverage analysis. Coverage reports include:

- **Line coverage**: Percentage of code lines executed by tests
- **Branch coverage**: Percentage of conditional branches tested
- **Class coverage**: Percentage of classes with at least one test

### Coverage Exclusions

The following packages are excluded from coverage reports:
- `**/mixin/**` - Mixin classes (tested through integration)
- `**/access/**` - Access widener interfaces

### Minimum Coverage Requirements

- Overall coverage: 50%
- Changed files in PRs: 50%

## GitHub Actions Workflow

The CI/CD pipeline performs the following steps:

1. **Build**: Compiles the project
2. **Test**: Runs all unit tests
3. **Coverage**: Generates JaCoCo coverage reports
4. **Artifacts**: Uploads test results and coverage reports
5. **PR Comments**: Posts coverage summary on pull requests
6. **GitHub Pages**: Deploys HTML coverage report (on main/master branch)

### Viewing Coverage Reports

#### In Pull Requests
Coverage information is automatically posted as a comment on each PR.

#### On GitHub Pages
After merging to the main branch, the latest coverage report is available at:
```
https://<username>.github.io/<repository>/
```

#### As Artifacts
Download coverage reports from the GitHub Actions run:
1. Go to Actions tab
2. Click on a workflow run
3. Download "coverage-reports" artifact

## Test Technologies

- **JUnit 5**: Primary testing framework
- **Mockito**: Mocking framework for unit tests
- **AssertJ**: Fluent assertion library
- **JaCoCo**: Code coverage tool

## Writing New Tests

### Basic Test Template

```java
package name.modid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyFeatureTest {
    
    @BeforeEach
    void setUp() {
        // Initialize test fixtures
    }
    
    @Test
    void testFeature() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Mocking Minecraft Objects

```java
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

@Mock
private ServerPlayerEntity mockPlayer;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockPlayer.getUuid()).thenReturn(UUID.randomUUID());
}
```

## Best Practices

1. **Test Naming**: Use descriptive names that explain what is being tested
2. **One Assertion Per Test**: Keep tests focused and simple
3. **AAA Pattern**: Arrange, Act, Assert
4. **Test Independence**: Each test should be able to run independently
5. **Mock External Dependencies**: Use Mockito to mock Minecraft objects
6. **Test Edge Cases**: Include tests for boundary conditions and error cases

## Continuous Integration

All tests must pass before code can be merged. The CI pipeline will:
- ✅ Run all tests
- ✅ Check code coverage
- ✅ Report results on PRs
- ❌ Fail the build if tests fail

## Troubleshooting

### Tests fail locally but pass in CI
- Ensure you're using Java 21
- Run `./gradlew clean test`
- Check for hardcoded paths or environment-specific code

### Coverage report not generated
- Ensure tests are passing first
- Run: `./gradlew clean test jacocoTestReport`
- Check `build/reports/jacoco/` directory

### Gradle permission denied
```bash
chmod +x gradlew
```

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Fabric Wiki - Testing](https://fabricmc.net/wiki/tutorial:testing)

