package com.chinacreator.c2.share;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

@Service
public class ShareDocService {
	
	@Transactional
	public void saveDoc(ShareDoc doc, List<ShareDocFile> fileList) {
		if (doc != null) {
			Dao<ShareDoc> docDao = DaoFactory.create(ShareDoc.class);
			if (doc.getId() != null) {
				docDao.update(doc);
			} else {
				//String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
				String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
				User user = OrgUserService.getUserInfoById(userId);
				String ownerName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
				String currentUserId = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
				doc.setAuthorId(currentUserId);
				docDao.insert(doc);
			}
		}
		if (!CollectionUtils.isEmpty(fileList)) {
			Dao<ShareDocFile> docFileDao = DaoFactory.create(ShareDocFile.class);
			for (ShareDocFile docFile : fileList) {
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
