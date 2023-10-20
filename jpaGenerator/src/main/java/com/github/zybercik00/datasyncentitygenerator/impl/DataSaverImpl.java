package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;
import lombok.extern.log4j.Log4j2;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Log4j2
public class DataSaverImpl implements DataSaver {

    private  final String outputDirectory = "v.outputDirectory";
    private final VelocityContextFactory velocityContextFactory;
    private final TemplateLoader templateLoader;

    public DataSaverImpl(VelocityContextFactory velocityContextFactory, TemplateLoader templateLoader) {
        this.velocityContextFactory = velocityContextFactory;
        this.templateLoader = templateLoader;
    }

    @Override
     public void emitJavaSources(List<JavaClass> classes) throws IOException {
        String outputPath = outputDirectory;
        Files.createDirectories(Paths.get(outputPath));
        for (JavaClass aClass : classes) {
            emitJavaSource(aClass, outputPath);
        }
    }


    private  void emitJavaSource(JavaClass aClass, String outputPath) throws IOException {
        Path javaSourcePath = getJavaSourcePath(aClass, outputPath);
        Files.createDirectories(javaSourcePath.getParent());
        log.info(() -> "Writing class " + aClass.getPackageName() + "." + aClass.getName() +
                " to file " + javaSourcePath.toAbsolutePath());

        Template template = templateLoader.getTemplate();
        VelocityContext context = velocityContextFactory.getVelocityContext(aClass);

        emitJavaSource(template, context, javaSourcePath);
        log.info(() -> "Class: " + aClass.getName() + " was written");
    }



    private  void emitJavaSource(
            Template template,
            VelocityContext context,
            Path javaSourcePath) throws IOException {
        try ( StringWriter writer = new StringWriter() ) {
            template.merge(context, writer);
            // TODO Format Java source before writing
            Files.writeString(javaSourcePath, writer.toString());
        }
    }

    private  Path getJavaSourcePath(JavaClass aClass, String outputPath) {
        String packageDir = aClass.getPackageName().replace('.', '/');
        Path packagePath = Paths.get(outputPath, packageDir);
        return packagePath.resolve(aClass.getName() + ".java");
    }
}
