""" Rules for Java.
"""

load("@bazel_skylib//lib:shell.bzl", "shell")

JavaCompileInfo = provider(
    "TODO: Docs",
    fields = {
        "jar": "resulting jar",
        "classes": "depset of class directories",
        "resources": "depset of resources",
    }
)

def format_classpath(jars):
    return ("-cp {jars}".format(
        jars = ":".join([shell.quote(jar.path) for jar in jars])
    )) if len(jars) > 0 else ""

def compile_classes(ctx, inputs, outdir, jars):
    cmd = "javac -d {outdir} {classpath} {inputs}".format(
        outdir = shell.quote(outdir.path),
        inputs = " ".join([shell.quote(src.path) for src in inputs]),
        classpath = format_classpath(jars),
    )
    ctx.actions.run_shell(
        outputs = [outdir],
        inputs = inputs + jars,
        command = cmd,
        mnemonic = "JavaCompile",
        use_default_shell_env = True,
    )

def make_jar(ctx, classes, output, deps, resources):
    """ Creates a jar

    Args:
        ctx: Bazel context
        classes: directories with classfiles
        output: output jarfile
        deps: directories with classfiles of dependencies
        resources: files for resources; will be in the root of the jar
    """
    cmd = "jar --create -f {output} -C {classes} .".format(
        output = shell.quote(output.path),
        classes = shell.quote(classes.path),
    )

    for dc in deps:
        cmd += " -C " + shell.quote(dc.path) + " ."

    for res in resources:
        cmd += " -C " + shell.quote(res.dirname) + " " + shell.quote(res.basename)

    ctx.actions.run_shell(
        outputs = [output],
        inputs = [classes] + deps + resources,
        command = cmd,
        mnemonic = "JarCreate",
        use_default_shell_env = True,
    )

def make_jar_executable(ctx, jar_in, jar_out, mainclass):
    cmd = "cp {jar_in} {jar_out}; jar --update -f {jar_out} -e {mainclass}".format(
        jar_in = shell.quote(jar_in.path),
        jar_out = shell.quote(jar_out.path),
        mainclass = mainclass,
    )

    ctx.actions.run_shell(
        outputs = [jar_out],
        inputs = [jar_in],
        command = cmd,
        mnemonic = "JarMakeExe",
        use_default_shell_env = True,
    )


def _java_library_impl(ctx):
    outdir = ctx.actions.declare_directory(ctx.label.name + "/classes")
    outjar = ctx.actions.declare_file(ctx.label.name + "/app.jar")
    classes_deps = depset([], transitive = [dep[JavaCompileInfo].classes for dep in ctx.attr.deps]).to_list()
    resources = depset(
        ctx.files.resources,
        transitive = [dep[JavaCompileInfo].resources for dep in ctx.attr.deps],
    )

    compile_classes(
        ctx,
        inputs = ctx.files.srcs,
        outdir = outdir,
        jars = classes_deps,
    )
    make_jar(
        ctx,
        classes = outdir,
        output = outjar,
        deps = classes_deps,
        resources = resources.to_list(),
    )

    return [
        DefaultInfo(files = depset([outdir, outjar])),
        JavaCompileInfo(
            jar = outjar,
            classes = depset(
                [outdir],
                transitive = [dep[JavaCompileInfo].classes for dep in ctx.attr.deps]
            ),
            resources = resources,
        )
    ]

java_library = rule(implementation = _java_library_impl, attrs = {
    "srcs": attr.label_list(allow_files = [".java"]),
    "deps": attr.label_list(providers = [JavaCompileInfo]),
    "resources": attr.label_list(allow_files = True),
})

def _java_binary_impl(ctx):
    out = ctx.actions.declare_file(ctx.label.name + "/app.jar")
    make_jar_executable(
        ctx,
        jar_in = ctx.attr.lib[JavaCompileInfo].jar,
        jar_out = out,
        mainclass = ctx.attr.mainclass,
    )
    return [DefaultInfo(files = depset([out]))]

java_binary = rule(implementation = _java_binary_impl, attrs = {
    "lib": attr.label(),
    "mainclass": attr.string(),
})

def extract_jar(ctx, jar, outdir):
    cmd = "unzip -d {outdir} {jar}".format(
        outdir = shell.quote(outdir.path),
        jar = shell.quote(jar.path),
    )
    ctx.actions.run_shell(
        outputs = [outdir],
        inputs = [jar],
        command = cmd,
        mnemonic = "JarExtract",
        use_default_shell_env = True,
    )

def _jar_library_impl(ctx):
    jar = ctx.file.jar
    classes = ctx.actions.declare_directory(ctx.label.name + "/classes")
    extract_jar(
        ctx,
        jar = jar,
        outdir = classes,
    )
    return [
        DefaultInfo(files = depset([classes])),
        JavaCompileInfo(
            jar = jar,
            classes = depset([classes]),
            resources = depset([]),
        )
    ]

jar_library = rule(implementation = _jar_library_impl, attrs = {
    "jar": attr.label(allow_single_file = [".jar"]),
})

