package weixin.popular.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import third.org.nutz.log.Log;
import third.org.nutz.log.Logs;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.material.Description;
import weixin.popular.bean.material.MaterialBatchgetResult;
import weixin.popular.bean.material.MaterialcountResult;
import weixin.popular.bean.material.NewsItem;
import weixin.popular.bean.media.Media;
import weixin.popular.bean.media.MediaType;
import weixin.popular.bean.message.Article;
import weixin.popular.client.LocalHttpClient;
import weixin.popular.util.JsonUtil;
import weixin.popular.util.StreamUtils;

/**
 * 永久素材
 * @author LiYi
 *
 */
public class MaterialAPI extends BaseAPI{
	
	private static final Log log = Logs.get();

	/**
	 * 新增永久图文素材
	 * @param access_token
	 * @param articles
	 * @return
	 */
	public static Media materialAdd_news(String access_token,List<Article> articles){
		String str = JsonUtil.toJSONString(articles);
		String messageJson = "{\"articles\":"+str+"}";
		HttpUriRequest httpUriRequest = RequestBuilder.post()
										.setHeader(jsonHeader)
										.setUri(BASE_URI+"/cgi-bin/material/add_news")
										.addParameter(getATPN(), access_token)
										.setEntity(new StringEntity(messageJson,Charset.forName("utf-8")))
										.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,Media.class);
	}


	/**
	 * 新增其他类型永久素材
	 * @param access_token
	 * @param mediaType
	 * @param media  	多媒体文件有格式和大小限制，如下：
						图片（image）: 2M，支持bmp/png/jpeg/jpg/gif格式
						语音（voice）：5M，播放长度不超过60s，支持AMR\MP3格式
						视频（video）：10MB，支持MP4格式
						缩略图（thumb）：64KB，支持JPG格式
	 * @param description 视频文件类型额外字段，其它类型不用添加
	 * @return
	 */
	public static Media materialAdd_material(String access_token,MediaType mediaType,File media,Description description){
		HttpPost httpPost = new HttpPost(BASE_URI+"/cgi-bin/material/add_material");
		FileBody bin = new FileBody(media);
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
        		 			.addPart("media", bin);
        if(description != null){
        	multipartEntityBuilder.addTextBody("description", JsonUtil.toJSONString(description));
        }
        HttpEntity reqEntity = multipartEntityBuilder.addTextBody(getATPN(), access_token)
			                 .addTextBody("type",mediaType.uploadType())
			                 .build();
        httpPost.setEntity(reqEntity);
		return LocalHttpClient.executeJsonResult(httpPost,Media.class);
	}

	/**
	 * 新增其他类型永久素材
	 * @param access_token
	 * @param mediaType
	 * @param inputStream  	多媒体文件有格式和大小限制，如下：
						图片（image）: 2M，支持bmp/png/jpeg/jpg/gif格式
						语音（voice）：5M，播放长度不超过60s，支持AMR\MP3格式
						视频（video）：10MB，支持MP4格式
						缩略图（thumb）：64KB，支持JPG格式
	 * @param description 视频文件类型额外字段，其它类型不用添加
	 * @return
	 */
	public static Media materialAdd_material(String access_token,MediaType mediaType,InputStream inputStream,Description description){
		HttpPost httpPost = new HttpPost(BASE_URI+"/cgi-bin/material/add_material");
		byte[] data = null;
		try {
			data = StreamUtils.copyToByteArray(inputStream);
		} catch (IOException e) {
			log.info("-materialAdd_material-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		}
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
        		.addBinaryBody("media",data,ContentType.DEFAULT_BINARY,"temp."+mediaType.fileSuffix());
		if(description != null){
			multipartEntityBuilder.addTextBody("description", JsonUtil.toJSONString(description));
		}
		HttpEntity reqEntity = multipartEntityBuilder.addTextBody(getATPN(), access_token)
		                 .addTextBody("type",mediaType.uploadType())
		                 .build();
        httpPost.setEntity(reqEntity);
		return LocalHttpClient.executeJsonResult(httpPost,Media.class);
	}


	/**
	 * 新增其他类型永久素材
	 * @param access_token
	 * @param mediaType
	 * @param uri  	多媒体文件有格式和大小限制，如下：
						图片（image）: 2M，支持bmp/png/jpeg/jpg/gif格式
						语音（voice）：5M，播放长度不超过60s，支持AMR\MP3格式
						视频（video）：10MB，支持MP4格式
						缩略图（thumb）：64KB，支持JPG格式
	 * @param description 视频文件类型额外字段，其它类型不用添加
	 * @return
	 */
	public static Media materialAdd_material(String access_token,MediaType mediaType,URI uri,Description description){
		HttpPost httpPost = new HttpPost(BASE_URI+"/cgi-bin/material/add_material");
		CloseableHttpClient tempHttpClient = HttpClients.createDefault();
		try {
			HttpEntity entity = tempHttpClient.execute(RequestBuilder.get().setUri(uri).build()).getEntity();
			 MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
					 .addBinaryBody("media",EntityUtils.toByteArray(entity),ContentType.get(entity),"temp."+mediaType.fileSuffix());
			 if(description != null){
				multipartEntityBuilder.addTextBody("description", JsonUtil.toJSONString(description));
			 }
			 HttpEntity reqEntity = multipartEntityBuilder.addTextBody(getATPN(), access_token)
			         .addTextBody("type",mediaType.uploadType())
			         .build();
			httpPost.setEntity(reqEntity);
			return LocalHttpClient.executeJsonResult(httpPost,Media.class);
		} catch (UnsupportedCharsetException e) {
			log.info("-error-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		} catch (ClientProtocolException e) {
			log.info("-error-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		} catch (ParseException e) {
			log.info("-error-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		} catch (IOException e) {
			log.info("-error-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		} finally{
			try {
				tempHttpClient.close();
			} catch (IOException e) {
				log.info("-error-"+e.getMessage(),e);
				log.info("-error-"+e.getMessage(),e);
			}
		}
		return null;
	}

	/**
	 * 获取永久素材
	 * @param access_token
	 * @param media_id
	 * @return
	 */
	public static byte[] materialGet_material(String access_token,String media_id){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(BASE_URI+"/cgi-bin/material/get_material")
					.addParameter(getATPN(), access_token)
					.setEntity(new StringEntity("{\"media_id\":\""+media_id+"\"}",Charset.forName("utf-8")))
					.build();
		CloseableHttpResponse httpResponse = LocalHttpClient.execute(httpUriRequest);
		try {
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (IOException e) {
			log.info("-error-"+e.getMessage(),e);
			log.info("-error-"+e.getMessage(),e);
		} finally{
			try {
				httpResponse.close();
			} catch (IOException e) {
				log.info("-error-"+e.getMessage(),e);
				log.info("-error-"+e.getMessage(),e);
			}
		}
		return null;
	}

	/**
	 * 获取永久素材 newsItem
	 * @param access_token
	 * @param media_id
	 * @return NewsItem
	 */
	public static NewsItem materialGet_material_newsItem(String access_token,String media_id){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(BASE_URI+"/cgi-bin/material/get_material")
					.addParameter(getATPN(), access_token)
					.setEntity(new StringEntity("{\"media_id\":\""+media_id+"\"}",Charset.forName("utf-8")))
					.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,NewsItem.class);
	}

	/**
	 * 删除永久素材
	 * @param access_token
	 * @param media_id
	 * @return
	 */
	public static BaseResult materialDel_material(String access_token,String media_id){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(BASE_URI+"/cgi-bin/material/del_material")
					.addParameter(getATPN(), access_token)
					.setEntity(new StringEntity("{\"media_id\":\""+media_id+"\"}",Charset.forName("utf-8")))
					.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,BaseResult.class);
	}

	/**
	 * 修改永久图文素材
	 * @param access_token
	 * @param media_id 	要修改的图文消息的id
	 * @param index 	要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
	 * @param articles
	 * @return
	 */
	public static BaseResult materialUpdate_news(String access_token,String media_id,int index,List<Article> articles){
		String str = JsonUtil.toJSONString(articles);
		String messageJson = "{\"media_id\":\""+media_id+"\",\"index\":"+index+",\"articles\":"+str+"}";
		HttpUriRequest httpUriRequest = RequestBuilder.post()
										.setHeader(jsonHeader)
										.setUri(BASE_URI+"/cgi-bin/material/update_news")
										.addParameter(getATPN(), access_token)
										.setEntity(new StringEntity(messageJson,Charset.forName("utf-8")))
										.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,BaseResult.class);
	}


	/**
	 * 获取素材总数
	 * @param access_token
	 * @return
	 */
	public static MaterialcountResult materialGet_materialcount(String access_token){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
										.setUri(BASE_URI+"/cgi-bin/material/get_materialcount")
										.addParameter(getATPN(), access_token)
										.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,MaterialcountResult.class);
	}


	/**
	 * 获取素材列表
	 * @param access_token
	 * @param type		素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
	 * @param offset	从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	 * @param count		返回素材的数量，取值在1到20之间
	 * @return
	 */
	public static MaterialBatchgetResult materialBatchget_material(String access_token,String type,int offset,int count){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
					.setUri(BASE_URI+"/cgi-bin/material/batchget_material")
					.addParameter(getATPN(), access_token)
					.setEntity(new StringEntity("{\"type\":\""+type+"\",\"offset\":"+offset+",\"count\":"+count+"}",Charset.forName("utf-8")))
					.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,MaterialBatchgetResult.class);
	}

}
