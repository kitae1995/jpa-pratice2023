package com.study.jpa.chap05_pratice.api;

import com.study.jpa.chap05_pratice.dto.*;
import com.study.jpa.chap05_pratice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Tag(name = "post API", description = "게시물 조회 등록 및 수정 , 삭제 API 입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    // 리소스: 게시물 (Post)
    /*
        게시물 목록 조회: /posts            - GET , param : (page,size)
        게시물 개별 조회: /posts/{id}       - GET
        게시물 등록:     /posts            - POST , payload: (writer, title, content, hashTags)
        게시물 수정:     /posts            - PUT , PATCH , payload : (title,content,postNo)
        게시물 삭제:     /posts/{id}       - DELETE
     */

    private final PostService postService;

    @GetMapping // 기본 requestmapping이 "/api/v1/posts"라 비워도 됨
    public ResponseEntity<?> list(PageDTO pageDTO) {
        log.info("/api/v1/posts?page={}&size={}",pageDTO.getPage(),pageDTO.getSize());

        PostListResponseDTO dto = postService.getPosts(pageDTO);

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id){
        log.info("/api/v1/posts/{} GET",id);

        try {
            PostDetailResponseDTO dto = postService.getDetail(id);
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }



    }

    @Operation(summary = "게시물 작성",description = "게시물 작성을 담당하는 메서드 입니다.")
    @Parameters({
            @Parameter(name = "writer" ,
                    description = "게시물의 작성자 이름을 쓰세요",example = "춘식이",required = true),
            @Parameter(name = "title" ,
                    description = "게시물의 제목을 쓰세요",example = "제목1",required = true),
            @Parameter(name = "content" ,
                    description = "게시물의 내용을 쓰세요",example = "내용 ~~~"),
            @Parameter(name = "hashTags" ,
                    description = "게시물의 해시태그를 쓰세요",example = "['하하','호호','후후']")


    })
    //게시물 등록
    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody PostCreateDTO dto
            , BindingResult result){
        //@Validated를 붙여야 PostCreateDTO 안에 있는 @NotNull과 같은 구문 작동
        //@RequestBody를 붙여야 json 객체를 자바로 변경 가능
        //BindingResult result -> 검증 에러 정보를 가진 객체
        log.info("/api/v1/posts POST!! - payload:{} " , dto);

        if(dto == null){
            return ResponseEntity.badRequest().body("등록 게시물 정보를 전달해 주세요");
        }

        ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
        if (fieldErrors != null) return fieldErrors;

        // 위에 존재하는 if문을 모두 건너뜀 -> dto가 null도 아니고, 입력값 검증도 모두 통과함.
        // -> 서비스에게 명령을 내림.
        try {
            PostDetailResponseDTO responseDTO = postService.insert(dto);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 터짐 원인 : " + e.getMessage());
        }



    }


    // 입력값 검증(Validation) 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if(result.hasErrors()){ // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(Error -> {
                log.warn("invalid client data - {} ", Error.toString());
            });
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return null;
    }

    // 게시물 수정
    @Operation(summary = "게시물 수정", description = "게시물 수정을 담당하는 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료!",
                    content = @Content(schema = @Schema(implementation = PostDetailResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")

    })
    @RequestMapping(method = {RequestMethod.PATCH , RequestMethod.PUT})
    // @PatchMapping ,@PutMapping 해도 됨 , 전통방식을 사용해본것
    public ResponseEntity<?> update(
            @Validated @RequestBody PostModifyDTO dto, BindingResult result,
            HttpServletRequest request)
        {
        log.info("/api/v1/posts {} - payload: {}", request.getMethod() , dto);

            ResponseEntity<List<FieldError>> fieldErrors = getValidatedResult(result);
            if(fieldErrors != null){
                return fieldErrors;
            }

            PostDetailResponseDTO responseDTO = postService.modify(dto);


            return ResponseEntity.ok().body(responseDTO);
        }






@DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        log.info("/api/v1/posts/{} delete!!" , id);

    try {
        postService.delete(id);
        return ResponseEntity.ok().body("delete succeess!");
    }
//    catch(SQLIntegrityConstraintViolationException e){
//        return ResponseEntity.internalServerError()
//                .body("해시태그가 달린 게시물은 삭제할 수 없습니다.");
//    }

    catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(e.getMessage());
    }


}
}
