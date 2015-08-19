Contributing
============

TridentSDK is a from-scratch, high-performance, and multithreaded Minecraft server alternative. One would probably looking at this file if they would like to contribute to the development of TridentSDK. There are a few tips, tricks, guidelines, and rules to look at and follow before submitting a Pull Request.

## Style Guidelines ##

See the [Trident Development Wiki Page](https://tridentsdkwiki.atlassian.net/wiki/display/DEV/TridentSDK+Project+Guidelines)

## Getting Started ##

1. Find or create a [JIRA issue](https://tridentsdk.atlassian.net/issues/?jql=project%20in%20(TRD%2C%20SDK)%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20priority%20DESC%2C%20updated%20DESC)
2. Await response from issue handlers
3. Fork the repository
4. [Write your changes](#code-requirements)
5. [Test the changes](#testing-requirements)
6. Push to the fork
7. Create a new Pull Request following the below guidlines

## Getting your PR accepted ##

We are more likely to accept your Pull Request should you follow all of the given bullet points listed below. That is:

- Your code meets the code requirements
- Your PR fits within the PR guidelines
- Your code is tested and provides testing materials
- Your PR adds or fixes a bug, and lies within the scope of the TridentSDK project

Once you have created your Pull Request, do not abandon GitHub for a week. We provide responses and code comments, as well as general discussion on the PR page. This feedback, along with proper action taken in response to our feedback will help improve your chances of getting the PR pulled.

## Code Requirements ##

- We expect all public members to be thouroughly documented
- If an instantiation requirement is outside the class, it must be documented
- Follows the [style guide](#style-guidelines)
- Compiles with Java 8
- Maven install runs correctly with the latest bleeding-edge commit
- Overriden methods need not have documentation

## PR Guidelines ##

- Upstream must be `bleeding-edge`
- Must contain title summarizing the changes
- Body contains description of the issue to be fixed, the implementation method, the methods of testing, and references (PRs, sources, stackoverflow, etc...)

## Testing Requirements ##

- If a performance fix is suggested, it must include JMH benchmarks and/or profiling data
- A player must join the server, stay on for 5 minutes, leave for 1 minute, join and stay on for 5 further minutes before deeming "joinable"
- The server must be shutdown with `stop` successfully
- Player framerate is very important - ensure it is stable and does not drop significantly
- Ensure that the `MultiplayerChunkCache` has 2 of the same values ({loaded}, {updates})
- Fly around and ensure chunks load correctly. Expiry threads must expire after 1 minute of inactivity.
- Ensure all required threads are active

## What To **NOT** Add ##

TridentSDK is a work in progress server reimplementation. We are open to PRs adding features in the Vanilla server that is not present in Trident. We are also open to new APIs, so that the server can be controlled by plugins in a way that was never possible before.

As such, there are things that are *out of the scope* of the TridentSDK project.

- Protocol APIs. We are continuing to discuss this. Currently, we have no maintainable way to provide a reliable API for controlling the Minecraft protocol. Packets continually change IDs, continually change the information contained in them, data types, etc... Please leave this in plugin requeusts.
- Support for other programming languages. This should be done through an interpreter, not through our API.
- Adds non-vanilla features. We reimplement the Vanilla server. This belongs in forks of Trident, not within the TridentSDK project itself.
- Your code contains content viewable from Mojang software. We are cleanroom. Please refrain from basing your content off of copyrighted code.

If the proposed PR adds to Trident what is described above, it does not fall within the scope of the TridentSDK project. Please refrain from creating Pull Requests of the content described. If you have any questions, they may be asked on our [Trident Development](https://tridentsdk.net/f/c/79/) forum.
