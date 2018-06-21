package org.collectiveone.modules.files;

public class FileStoredDto {

	private String id;
	private String modelSectionId;
	private String modelSectionName;
	private String uploadedById;
	private String bucket;
	private String key;
	private String url;
	private Long lastUpdated;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getModelSectionId() {
		return modelSectionId;
	}
	public void setModelSectionId(String modelSectionId) {
		this.modelSectionId = modelSectionId;
	}
	public String getModelSectionName() {
		return modelSectionName;
	}
	public void setModelSectionName(String modelSectionName) {
		this.modelSectionName = modelSectionName;
	}
	public String getUploadedById() {
		return uploadedById;
	}
	public void setUploadedById(String uploadedById) {
		this.uploadedById = uploadedById;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
		
}
