Contributing
============

TridentSDK is a from-scratch, high-performance, and multithreaded Minecraft server alternative. One would probably looking at this file if they would like to contribute to the development of TridentSDK. There are a few tips, tricks, guidelines, and rules to look at and follow before submitting a Pull Request.

## Style Guidelines ##

Unless otherwise specified below, we follow [Google's Java Style Guide](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html) style.

In addition to the above link, below is a brief summary of the key points and changes.

### Format

- 4 space indentation. Do not use tabs
- 80 character limit per line
- K & R Style brackets
- The copyright notice appears at the very top of the file, followed by an empty line, followed by the package
- No whitespace between first field declaration and class declaration
- Always include ```@author The TridentSDK Team``` as the last item in the class javadoc
- Indent as specified in §4 of the Google Style Guidelines (except indentation is 4 spaces instead of 2).
- No whitespace after closing brace of class, no empty line before closing brace.
- New line after ```;```, do not put multiple statements on one line, except for ```for``` statements
- Keywords and braces have whitespace around them
- All operators have surrounding whitespace
- All ```for``` statements have whitespace after the ```;```, always have whitespace after commas ```,```

### Naming

- Managers or handling classes are named ```{Function}Handler```
- Package names are always lowercase and singular
- **NEVER** use underscores ```_``` except for ```CONSTANT_FIELDS``` and enumerations
- Use shortened field names sparingly
- Abbreviations allowed for local variables
- Classes go by CapitalCamelCase
- Class names that contain an abbreviation are discouraged, except for common abbreviations
- Abbreviations follow standard Camel-case (e.g. ID becomes id or Id, HTTP becomes http or Http, etc.)
- Interfaces have the same casing and rules as class names
- Constants declared static and final, and their names must be all uppercase, with underscores separating words
- Methods and fields use lowercaseCamelCase

### Technical Design

- Thread safe classes use static factories
- Immutable classes marked with ```@ThreadSafe```
- Use ```@GuardedBy``` annotations if synchronization is used
- **IMPLEMENTED API METHODS MUST BE THREAD SAFE**
- Fields made final whenever it possible
- Initialize as soon as possible, unless object contains heavy computation. Mark for discussion.

### Documentation

- Should be in the following format: description, whitespace, then tags
- Only field comments are allowed to be single line comments
- Overridden methods do not need documentation, as long as the superclass/superinterface is documented
- Classes are always documented unless they are package private
- Package private classes are marked with ```@AccessNoDoc```. Do not document them.
- ```@InternalUseOnly``` members may not be documented, should not be used in API classes/interfaces
- All packages must include a ```package-info.html``` to describe/list classes in that package
- API documentation should not include implementation details
- Use ```@Volatile``` to document an unstable or non-conventional usage of a particular member
- Prefer ```@throws``` over ```@exception```, and ```@code``` over ```<code>```
- First mention of another class that is in Trident or TridentSDK should be linked
- Java keywords should have ```@code``` tags
- Document return of null, or possible usage of null in parameters

### Practices
- Use ```this``` qualifiers for method calls or field references, where it improves readablity
- Do not qualify ```this``` for inner classes, as it generates ```{Enclosing class}.this.{reference}```
- Static qualifiers are not needed if used inside the class (e.g. INFO instead of TridentServer.INFO inside the class TridentServer)
- Use parentheses when they improve readability or provide clarity
- Any ignored (swallowed) exceptions must be commented to explain why
- Use ```TridentLogger.get().error(Exception)``` when handling exceptions, unless breakpoint is needed
- Any empty conditionals must have a comment explaining why they are empty

## Getting Started ##

1. Find or create a [JIRA issue](https://tridentsdk.atlassian.net/issues/?jql=project%20in%20(TRD%2C%20SDK)%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC)
2. Await response from issue handlers
3. Fork the repository
4. [Write your changes](#code-requirements)
5. [Test the changes](#testing-requirements)
6. Push to the fork
7. Create a new Pull Request following the below guidlines

## Code Requirements ##

- We expect all public members to be thouroughly documented
- If an instantiation requirement is outside the class, it must be documented
- Follows the [style guide](#style-guidelines)
- Compiles with Java 8
- Maven install runs correctly with the latest bleeding-edge commit

## PR Guidelines ##

- Upstream must be `bleeding-edge`
- Must contain title summarizing the changes
- Body contains description of the issue to be fixed, the implementaiotn method, the methods of testing, and references

## Testing Requirements ##

## What To **NOT** Add ##

WIP
