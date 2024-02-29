package com.example.demo.feed;

import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;


    public List<Feed> findAll() {
        return this.feedRepository.findAll();
    }

    public void create(String title, String content, String area, String address, User loginUser, MultipartFile file) throws IOException {
        Feed feed=new Feed();
        feed.setTitle(title);
        feed.setContent(content);
        feed.setArea(area);
        feed.setAddress(address);
        feed.setAuthor(loginUser);

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        feed.setFilename(fileName);
        feed.setFilePath("/files/" + fileName);
//        feed.setFilePath(fileName);
        this.feedRepository.save(feed);
    }
//    Optional<ShopEntity> shopEntity = this.shopRepository.findById(id);
//        if (shopEntity.isPresent()) {
//        return shopEntity.get();
//    } else {
//        throw new DataNotFoundException("item not found!!!");
//    }

    public Feed getFeed(Long id) {
        Optional<Feed> feed = this.feedRepository.findById(id);
        if(feed.isPresent()){
            return feed.get();
        }else {
            throw new RuntimeException();
        }
    }
}
