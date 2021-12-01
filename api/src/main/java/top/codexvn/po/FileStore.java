package top.codexvn.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStore {
  private String UUID;
  private long fileSize;
  private String fileType;
  private String fileName;
  private long createTime;
//  @JsonIgnore
  private String url;
}
