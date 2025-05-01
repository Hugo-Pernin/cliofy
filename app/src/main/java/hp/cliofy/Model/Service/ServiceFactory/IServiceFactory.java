package hp.cliofy.Model.Service.ServiceFactory;

import hp.cliofy.Model.Service.AlbumService.IAlbumService;
import hp.cliofy.Model.Service.ArtistService.IArtistService;
import hp.cliofy.Model.Service.AuthenticationService.IAuthenticationService;
import hp.cliofy.Model.Service.PlayerService.IPlayerService;
import hp.cliofy.Model.Service.PlaylistService.IPlaylistService;
import hp.cliofy.Model.Service.TrackService.ITrackService;
import hp.cliofy.Model.Service.UserService.IUserService;

public interface IServiceFactory {
    IAlbumService createAlbumService(String accessToken);
    IArtistService createArtistService(String accessToken);
    IAuthenticationService createAuthenticationService();
    IPlayerService createPlayerService(String accessToken);
    IPlaylistService createPlaylistService(String accessToken);
    ITrackService createTrackService(String accessToken);
    IUserService createUserService(String accessToken);
}
