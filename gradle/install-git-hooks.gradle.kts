tasks.register<Copy>("initGitHooks") {
    group = "build setup"
    description = "Put team git hooks to .git/hooks directory"

    from(file("config/git-hooks/pre-commit"))
    into(file(".git/hooks"))
    fileMode = Integer.parseUnsignedInt("755", 8)
}
