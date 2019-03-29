package com.chinacreator.c2.share;

import java.util.List;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.fs.FileServer;
import com.chinacreator.c2.ioc.ApplicationContextManager;

public class ShareDocFileService {
	
	public List<ShareDocFile> getDocFiles(ShareDoc doc) {
		Dao<ShareDocFile> docFileDao = DaoFactory.create(ShareDocFile.class);
		ShareDocFile docFile = new ShareDocFile();
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
