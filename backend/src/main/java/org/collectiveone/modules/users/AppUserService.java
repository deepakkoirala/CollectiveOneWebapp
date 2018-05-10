package org.collectiveone.modules.users;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.collectiveone.common.dto.GetResult;
import org.collectiveone.common.dto.PostResult;
import org.collectiveone.modules.activity.ActivityService;
import org.collectiveone.modules.activity.Subscriber;
import org.collectiveone.modules.activity.enums.SubscriberInheritConfig;
import org.collectiveone.modules.activity.enums.SubscriptionElementType;
import org.collectiveone.modules.activity.repositories.SubscriberRepositoryIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;

@Service
public class AppUserService {
	
	@Autowired
	private AppUserRepositoryIf appUserRepository;
	
	@Autowired
	private AppUserProfileRepositoryIf appUserProfileRepository;
	
	@Autowired
	private ManagementAPI mgmt;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private SubscriberRepositoryIf subscriberRepository;
	

	@Transactional
	public AppUser getOrCreateFromAuth0Id(String auth0Id) {
		
		AppUser appUser = appUserRepository.findByAuth0Id(auth0Id);
		
		if(appUser == null) {
			/* the first time a user is requested, it 
			 * is added to the local DB */
			appUser = addUserToLocalDB(auth0Id);
		} else {
			appUser.setLastSeen(new Timestamp(System.currentTimeMillis()));
			appUserRepository.save(appUser);
		}
    	
    	return appUser;
	}
	
	@Transactional
	public AppUser getFromAuth0Id(String auth0Id) {
		AppUser appUser = appUserRepository.findByAuth0Id(auth0Id);
		if (appUser != null) {
			appUser.setLastSeen(new Timestamp(System.currentTimeMillis()));
			appUserRepository.save(appUser);	
		}
    	return appUser;
	}
	
	@Transactional
	public AppUser getFromC1Id(UUID c1Id) {
		return appUserRepository.findByC1Id(c1Id);
	}
	
	@Transactional
	public GetResult<AppUserDto> getUserLight(UUID c1Id) {
		return new GetResult<AppUserDto>("success", "user profile retrieved", getFromC1Id(c1Id).toDtoLight());
	}
	
	@Transactional
	public GetResult<AppUserDto> getUserFull(UUID c1Id) {
		return new GetResult<AppUserDto>("success", "user profile retrieved", getFromC1Id(c1Id).toDtoFull());
	}
	
	@Transactional
	public PostResult editUserProfile(UUID c1Id, AppUserDto userDto) {
		AppUserProfile profile = appUserProfileRepository.findByUser_C1Id(c1Id);
		
		if (profile.getUsername() != null) {
			if (!profile.getUsername().equals(userDto.getUsername())) {
				/* check if username exist */
				AppUserProfile profileOfUsername = appUserProfileRepository.findByUsername(userDto.getUsername());
				
				if (profileOfUsername != null) {
					return new PostResult("error", "username already exist", "");
				}
			}
		}
		
		profile.setNickname(userDto.getNickname());
		profile.setUsername(userDto.getUsername());
		profile.setPictureUrl(userDto.getPictureUrl());
		profile.setShortBio(userDto.getShortBio());
		profile.setTwitterHandle(userDto.getTwitterHandle());
		profile.setFacebookHandle(userDto.getFacebookHandle());
		profile.setLinkedinHandle(userDto.getLinkedinHandle());
		profile.setUseUploadedPicture(userDto.getUseUploadedPicture());
		
		appUserProfileRepository.save(profile);
		
		return new PostResult("success", "profile edited", profile.getId().toString());
	}
	
	
	@Transactional
	public GetResult<Boolean> usernameExist(String username) {
		AppUserProfile profile = appUserProfileRepository.findByUsername(username);
		return new GetResult<Boolean>("success", "existance cheked", profile != null);
	}
	
	

	@Transactional
	public GetResult<List<AppUserDto>> searchBy(String q) {
		List<AppUser> appUsers = appUserRepository.findTop10ByProfile_NicknameLikeIgnoreCase("%"+q+"%");
		
		List<AppUserDto> appUserDtos = new ArrayList<AppUserDto>();
		
		for(AppUser appUser : appUsers) {
			appUserDtos.add(appUser.toDtoLight());
		}
		
		return new GetResult<List<AppUserDto>>("succes", "initiatives returned", appUserDtos);
	}
	
	@Transactional
	public Boolean updateUserDataInLocalDB(UUID c1Id) {
		AppUser appUser = appUserRepository.findByC1Id(c1Id);
		
		try {
			User auth0User = mgmt.users().get(appUser.getAuth0Ids().get(0), null).execute();
			appUser.getProfile().setPictureUrl(auth0User.getPicture());
			appUserRepository.save(appUser);
			
			return true;
			
		} catch (APIException exception) {
		    System.out.println(exception.getMessage());
		} catch (Auth0Exception exception) {
			System.out.println(exception.getMessage());
		}
		
		return false;
		
	}
	
	@Transactional
	private AppUser addUserToLocalDB(String auth0Id) {
		/* retrieve from Auth0 */
		AppUser appUser = null;
		User auth0User = null;
		
		if (auth0Id.equals("anonymousUser")) {
			return null;
		}
		
		try {
			auth0User = mgmt.users().get(auth0Id, null).execute();
			
			/* check if this email is already registered. */
			appUser = appUserRepository.findByEmail(auth0User.getEmail());
			
			if (appUser == null) {
				// if (auth0User.isEmailVerified()) {
				 if (true) {
					/* create a new user if not */
					appUser = new AppUser();
					
					appUser.getAuth0Ids().add((auth0User.getId()));
					appUser.setEmail(auth0User.getEmail());
					appUser.setEmailNotificationsEnabled(true);
					
					AppUserProfile profile = new AppUserProfile();
					
					if (auth0User.getIdentities().get(0).getProvider().equals("auth0")) {
						profile.setNickname(auth0User.getNickname());
					} else {
						profile.setNickname(auth0User.getName());
					}
					
					profile.setUser(appUser);
					profile.setPictureUrl(auth0User.getPicture());
					profile = appUserProfileRepository.save(profile);
					
					appUser.setProfile(profile);
					
					/* create global subscriber */
					Subscriber subscriber = new Subscriber();
					
					subscriber.setType(SubscriptionElementType.COLLECTIVEONE);
					subscriber.setUser(appUser);
					
					subscriber.setInheritConfig(SubscriberInheritConfig.CUSTOM);
					
					activityService.initDefaultSubscriber(subscriber);
					
					subscriberRepository.save(subscriber);
					
				} 
			} else {
				/* just add the auth0id to the existing user */
				appUser.getAuth0Ids().add(auth0Id); 
			}
			
			appUser = appUserRepository.save(appUser);
			
		} catch (APIException exception) {
		    System.out.println(exception.getMessage());
		} catch (Auth0Exception exception) {
			System.out.println(exception.getMessage());
		}
		
		return appUser;
	} 
	
	@Transactional
	public PostResult disableEmailNotifications(UUID userId) {
		AppUser user = appUserRepository.findByC1Id(userId);
		user.setEmailNotificationsEnabled(false);
		return new PostResult("success", "email notifications disabled", "");
	}

}
