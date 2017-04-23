package org.warp.midito3d.printers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GCodeOutput {
	
	Path path;
	BufferedWriter bw;
	
	public GCodeOutput(String path) throws URISyntaxException {
		this.path = new File(path).toPath();
	}

	public void openAndLock() throws IOException {
		Files.deleteIfExists(path);
		Files.createFile(path);
		bw = Files.newBufferedWriter(path);
		bw.flush();
	}

	public void write(String string) throws IOException {
		bw.write(string);
	}

	public void writeLine(String string) throws IOException {
		write(string+"\n");
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();
		bw = null;
	}

}
