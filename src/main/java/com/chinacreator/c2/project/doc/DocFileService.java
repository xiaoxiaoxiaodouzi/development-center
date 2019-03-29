package com.chinacreator.c2.project.doc;

import java.util.List;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.fs.FileServer;
import com.chinacreator.c2.ioc.ApplicationContextManager;

public class DocFileService {
	public List<DocFile> getDocFiles(Doc doc) {
		Dao<DocFile> docFileDao = DaoFactory.create(DocFile.class);
		DocFile docFile = new DocFile();
		docFile.setDocId(doc.getId());
		return docFileDao.select(docFile);
	}

	public void delFile(String path) throws Exception {
		FileServer server = ApplicationContextManager.getContext().getBean(
				"fileServer", FileServer.class);
		if (server.exsits(path)) {
			server.delete(path, true);
		}
	}
}
