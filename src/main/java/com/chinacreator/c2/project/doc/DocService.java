package com.chinacreator.c2.project.doc;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.MembersMgt;

@Service
public class DocService {
	@Transactional
	public void saveDoc(Doc doc, List<DocFile> fileList) {
		if (doc != null) {
			Dao<Doc> docDao = DaoFactory.create(Doc.class);
			if (doc.getId() != null) {
				docDao.update(doc);
			} else {
				String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
				String currentUserId = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
				doc.setAuthorId(currentUserId);
				docDao.insert(doc);
			}
		}
		if (!CollectionUtils.isEmpty(fileList)) {
			Dao<DocFile> docFileDao = DaoFactory.create(DocFile.class);
			for (DocFile docFile : fileList) {
				if (docFile.getDocId()==null) {
					docFile.setDocId(doc.getId());
				}
				if (docFile.getId() != null) {
					docFileDao.update(docFile);
				} else {
					docFileDao.insert(docFile);
				}
			}
		}
	}
}
