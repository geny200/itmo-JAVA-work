package ru.ifmo.rain.konovalov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;

public class JarImplementor implements JarImpler {
    public static void main(String[] args) {
        if (args == null || args.length < 2 || args[0] == null || args[1] == null || args.length >= 3 && args[2] == null) {
            System.out.println("Error: null input argument");
            return;
        }
        JarImplementor implementor = new JarImplementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                if (!args[0].equals("jar")) {
                    System.out.println("Error: '" + args[0] + "' isn't a Implementor command");
                    return;
                }
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (InvalidPathException e) {
            System.out.println("Error: Incorrect path to root: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Incorrect class name: " + e.getMessage());
        } catch (ImplerException e) {
            System.out.println("Implementation Error: " + e.getMessage());
        }
    }

    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        } catch (Exception e) {
            return token.getPackage().getName().replace(".", File.separator);
        }
    }

    private static String getImplName(final Class<?> token) {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

    public static Path getFile(final Path root, final Class<?> clazz) {
        return root.resolve(getImplName(clazz).replace(".", File.separator) + ".java").toAbsolutePath();
    }

    public static void compileFiles(final Path root, final String file, Class<?> token) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String[] argsStr = {"-encoding", "cp866", "-cp", root + File.pathSeparator + getClassPath(token), file};
        final int exitCode = compiler.run(null, null, null, argsStr);
    }

    public static void compile(final Path root, final Class<?> token) {
        compileFiles(root, getFile(root, token).toString(), token);
    }

    private String getStrFromArray(Class<?>[] clazz, Function<Integer, String> toStr) {
        return IntStream.range(0, clazz.length).mapToObj(i -> clazz[i].getCanonicalName() + toStr.apply(i + 1)).collect(Collectors.joining(", "));
    }

    private String getExceptions(Class<?>[] clazz) {
        if (clazz.length == 0)
            return "";
        return " throws " + getStrFromArray(clazz, i -> "");
    }

    private String getReturn(Class<?> clazz) {
        if (void.class.equals(clazz))
            return "return";
        else if (boolean.class.equals(clazz))
            return "return false";
        else if (clazz.isPrimitive())
            return "return 0";
        else
            return "return null";
    }

    private String getStrFromExecutable(Executable method, String name, String returnName, String body) {
        return String.format(
                "\t%s %s %s(%s)%s{ \n\t\t%s;\n\t}\n\n",
                Modifier.toString(method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT),
                returnName,
                name,
                getStrFromArray(method.getParameterTypes(), i -> " var" + i),
                getExceptions(method.getExceptionTypes()),
                body
        );
    }

    private String getStrFromMethod(Method method) {
        return getStrFromExecutable(
                method,
                method.getName(),
                method.getReturnType().getCanonicalName(),
                getReturn(method.getReturnType())
        );
    }

    private String getConstructor(String className, Constructor<?> constructor) {
        if (constructor == null)
            return "";
        return getStrFromExecutable(
                constructor,
                className,
                "",
                String.format("super(%s)",
                        IntStream.range(1, constructor.getParameterCount() + 1)
                                .mapToObj(i -> "var" + i)
                                .collect(Collectors.joining(", "))));
    }

    private String getPackage(Class<?> token) {
        if (token.getPackage() == null)
            return "";
        return "package " + token.getPackageName() + ";\n";
    }

    private StringBuilder getProtectedMethods(Class<?> clazz) {
        if (clazz == null)
            return new StringBuilder();

        StringBuilder result = getProtectedMethods(clazz.getSuperclass());
        for (Method item : clazz.getDeclaredMethods()) {
            int mod = item.getModifiers();
            if (Modifier.isAbstract(mod) && !Modifier.isPublic(mod))
                result.append(getStrFromMethod(item));
        }
        return result;
    }

    private StringBuilder getMethods(Class<?> clazz) {
        StringBuilder result = getProtectedMethods(clazz);
        for (Method item : clazz.getMethods())
            if (Modifier.isAbstract(item.getModifiers()))
                result.append(getStrFromMethod(item));
        return result;
    }

    /**
     * Produces code implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <var>root</var> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <var>$root/java/util/ListImpl.java</var>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     *                                                                 generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token == null || root == null)
            throw new ImplerException("Null arguments");
        else if (Enum.class.equals(token))
            throw new ImplerException("Can't implement enum");
        else if (token.isPrimitive())
            throw new ImplerException("Can't implement primitive type");
        else if (token.isArray())
            throw new ImplerException("Can't implement array");
        else if (Modifier.isFinal(token.getModifiers()))
            throw new ImplerException("Can't extend final");
        else if (Modifier.isPrivate(token.getModifiers()))
            throw new ImplerException("Can't extend private");

        if (token.getPackage() != null) {
            root = root.resolve(token.getPackageName().replace(".", File.separator));
        }
        Path javaFile = root.resolve(token.getSimpleName() + "Impl.java");

        if (!Files.exists(javaFile))
            try {
                Files.createDirectories(root);
                Files.createFile(javaFile);
            } catch (IOException e) {
                throw new ImplerException("Can't create java file" + e.getMessage());
            }

        Constructor<?> constructor = null;
        if (!token.isInterface()) {
            for (Constructor<?> item : token.getDeclaredConstructors())
                if (!Modifier.isPrivate(item.getModifiers())) {
                    constructor = item;
                    break;
                }
            if (constructor == null)
                throw new ImplerException("Can't created private constructor");
        }

        try (BufferedWriter os = Files.newBufferedWriter(javaFile)) {
            os.write(toUnicode(
                    String.format(
                            "%s\npublic class %sImpl %s %s {" + System.lineSeparator() + "%s%s};",
                            getPackage(token),
                            token.getSimpleName(),
                            token.isInterface() ? "implements" : "extends",
                            token.getCanonicalName(),
                            getConstructor(token.getSimpleName() + "Impl", constructor),
                            getMethods(token)
                    )));
        } catch (IOException e) {
            System.err.println("An OutPut error occurred:" + e.getMessage());
        }
    }

    private String toUnicode(String target) {
        StringBuilder escapeBuilder = new StringBuilder();
        for (char c : target.toCharArray()) {
            if (c >= 128)
                escapeBuilder.append(String.format("\\u%04X", (int) c));
            else
                escapeBuilder.append(c);
        }
        return escapeBuilder.toString();
    }

    /**
     * Produces <var>.jar</var> file implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token   type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path temp = Paths.get("temp"), deleteTemp = temp;
        if (token.getPackage() != null) {
            deleteTemp = temp.resolve(token.getPackageName().replace(".", File.separator));
        }
        Path compilePath = temp.resolve(getImplName(token).replace(".", File.separator) + ".class");
        String className = getImplName(token).replace(".", "/") + ".class";

        LinkedList<File> deleteList = new LinkedList<>();
        deleteList.add(compilePath.toFile());
        deleteList.add(deleteTemp.resolve(token.getSimpleName() + "Impl.java").toFile());

        while (deleteTemp != null && !deleteTemp.toString().isEmpty()) {
            deleteList.add(deleteTemp.toFile());
            deleteTemp = deleteTemp.getParent();
        }

        for (Iterator<File> iterator = deleteList.descendingIterator(); iterator.hasNext(); )
            iterator.next().deleteOnExit();

        implement(token, temp);
        compile(temp, token);

        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "Geny200");

        try (JarOutputStream os = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            os.putNextEntry(new ZipEntry(className));
            Files.copy(compilePath, os);
        } catch (IOException e) {
            throw new ImplerException("couldn't write to jar file");
        }
    }
}
